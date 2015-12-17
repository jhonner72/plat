package com.fujixerox.aus.asset.impl.processor.inquiry.imaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOException;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.dstc.security.util.Base64InputStream;
import com.fujixerox.aus.asset.api.processor.IFallbackDataProvider;
import com.fujixerox.aus.asset.api.util.CoreUtils;
import com.fujixerox.aus.asset.api.util.cache.ThreadLocalCache;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.imaging.ImageBean;
import com.fujixerox.aus.asset.imaging.ImageTransCoder;
import com.fujixerox.aus.asset.impl.constants.DocumentumConstants;
import com.fujixerox.aus.asset.model.beans.generated.request.GetImagePage;
import com.fujixerox.aus.asset.model.beans.generated.request.IndexQuery;
import com.fujixerox.aus.asset.model.beans.generated.request.Inquiry;
import com.fujixerox.aus.asset.model.beans.generated.request.ProcessContent;
import com.fujixerox.aus.asset.model.beans.generated.request.RequestedContentDescriptor;
import com.fujixerox.aus.asset.model.beans.generated.request.TranscodeContent;
import com.fujixerox.aus.asset.model.beans.generated.request.YNBool;
import com.fujixerox.aus.asset.model.beans.generated.response.EncodeType;
import com.fujixerox.aus.asset.model.beans.generated.response.GetImagePageResult;
import com.fujixerox.aus.asset.model.beans.generated.response.Image;
import com.fujixerox.aus.asset.model.beans.generated.response.Item;
import com.fujixerox.aus.asset.model.beans.generated.response.ProcessContentResult;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ImageProcessor {

    private final Inquiry _request;

    private final IndexQuery _query;

    private final IFallbackDataProvider _fallbackDataProvider;

    public ImageProcessor(Inquiry request, IndexQuery query,
            IFallbackDataProvider fallbackDataProvider) {
        _request = request;
        _query = query;
        _fallbackDataProvider = fallbackDataProvider;
    }

    private ProcessContent getProcessContent() {
        List<ProcessContent> processContents = _query.getProcessContents();
        if (processContents == null || processContents.isEmpty()) {
            processContents = _request.getProcessContents();
        }

        if (processContents == null || processContents.isEmpty()) {
            return null;
        }

        if (processContents.size() > 1) {
            Logger
                    .error("Request contains multiple ProcessContent directives, using first one");
        }

        return processContents.get(0);
    }

    private boolean canProcess(IDfTypedObject row) throws DfException {
        if (_request.getImages() != YNBool.Y) {
            return false;
        }

        if (_request.getCount() == YNBool.Y) {
            return false;
        }

        return row.hasAttr(DocumentumConstants.R_OBJECT_ID);
    }

    private boolean hasGetImageRequest(ProcessContent processContent) {
        return !(CoreUtils.isEmpty(processContent.getGetImagePages()) && CoreUtils
                .isEmpty(processContent.getGetTiffPages()));
    }

    public void processImages(IDfTypedObject row, Item item) throws DfException {
        if (!canProcess(row)) {
            return;
        }
        InputStream content = getContent(row);
        processImages(content, item);
    }

    public void processNoResultImage(Item item) throws DfException {
        try {
            processImages(_fallbackDataProvider.getNoResultImage(), item);
        } catch (IOException ex) {
            throw new DfException(ex);
        }
    }

    private void processImages(InputStream content, Item item)
        throws DfException {
        ProcessContent processContent = getProcessContent();

        if (processContent == null) {
            return;
        }

        ProcessContentResult processContentResult = new ProcessContentResult();

        if (!hasGetImageRequest(processContent)) {
            processContentResult.getImages().add(getUnProcessedImage(content));
            item.getProcessContentResults().add(processContentResult);
            return;
        }

        for (GetImagePage imagePage : processContent.getGetImagePages()) {
            List<GetImagePageResult> result = getAllImages(content, imagePage);
            if (result != null && !result.isEmpty()) {
                processContentResult.withGetImagePageResults(result);
            }
        }

        for (GetImagePage imagePage : processContent.getGetTiffPages()) {
            List<GetImagePageResult> result = getAllImages(content, imagePage);
            if (result != null && !result.isEmpty()) {
                processContentResult.withGetTiffPageResults(result);
            }
        }

        item.getProcessContentResults().add(processContentResult);
    }

    private String getRequestedFormat(GetImagePage imagePage) {
        TranscodeContent transcodeContent = imagePage.getTranscodeContent();
        if (transcodeContent == null) {
            return null;
        }
        RequestedContentDescriptor descriptor = transcodeContent
                .getRequestedContentDescriptor();
        if (descriptor == null) {
            return null;
        }
        return descriptor.value();
    }

    private Image getUnProcessedImage(InputStream content) throws DfException {
        try {
            return makeImage(content);
        } catch (IOException ex) {
            throw new DfException(ex);
        }
    }

    private List<GetImagePageResult> getAllImages(InputStream content,
            GetImagePage imagePage) throws DfException {
        try {
            PageIterator pages = new PageIterator(imagePage.getPages());
            ImageTransCoder transCoder = getTransCoder(content, pages);
            List<GetImagePageResult> result = new ArrayList<GetImagePageResult>();
            for (int i = 0, n = transCoder.getImageCount(); i < n; i++) {
                if (!transCoder.hasImage(i)) {
                    continue;
                }
                Image image = makeImage(transCoder.writeImage(
                        getRequestedFormat(imagePage), i));
                result.add(new GetImagePageResult().withImage(image)
                        .withPageNumber(i + 1));
            }
            return result;
        } catch (IOException ex) {
            throw new DfException(ex);
        } finally {
            IOUtils.closeQuietly(content);
        }
    }

    private ImageTransCoder createTransCoder(InputStream content)
        throws IIOException {
        ImageTransCoder transCoder;
        try {
            transCoder = new ImageTransCoder();
            if (content == null) {
                Logger.debug("Stream is null");
            } else {
                transCoder.addImage(content);
            }
        } catch (IIOException ex) {
            if (_fallbackDataProvider == null) {
                Logger
                        .error(
                                "Can't decode image and no fallback provider specified",
                                ex);
                throw ex;
            }
            try {
                InputStream fallback = _fallbackDataProvider.getFallBackImage();
                if (fallback == null) {
                    Logger
                            .error(
                                    "Can't decode image and fallback provider does not return fallback image",
                                    ex);
                    throw ex;
                }
                transCoder = new ImageTransCoder();
                transCoder.addImage(fallback);
                Logger
                        .debug(
                                "Using fallback image due to exception while adding image",
                                ex);
            } catch (Exception local) {
                Logger
                        .error(
                                "Exception occurred while trying to use fallback image",
                                local);
                throw ex;
            }
        }
        return transCoder;
    }

    ImageTransCoder getTransCoder(InputStream content, PageIterator pages)
        throws IOException {
        ImageTransCoder transCoder = createTransCoder(content);
        ImageBean blankPage = null;
        int current = 0;
        while (pages.hasNext()) {
            int pageNo = pages.next() - 1;
            if (transCoder.getImageCount() <= pageNo
                    && pageNo > pages.getMaxPage() - 1) {
                break;
            }
            // skip not requested pages
            for (; current < pageNo; current++) {
                Logger.debug("Using null for page " + (current + 1));
                transCoder.setImage(null, current);
            }
            // skip not requested pages
            if (!pages.hasNext()) {
                for (; current + 1 < transCoder.getImageCount(); current++) {
                    Logger.debug("Using null for page " + (current + 2));
                    transCoder.setImage(null, current + 1);
                }
            }

            // set blank page
            if (!transCoder.hasImage(pageNo)) {
                if (blankPage == null) {
                    blankPage = createBlankPage(transCoder);
                }
                if (blankPage == null) {
                    Logger.debug("Using null for page " + (pageNo + 1));
                } else {
                    Logger.debug("Using blank page for page " + (pageNo + 1));
                }
                transCoder.setImage(blankPage, pageNo);
            }
            current = pageNo + 1;
        }

        return transCoder;
    }

    private ImageBean createBlankPage(ImageTransCoder transCoder)
        throws IOException {
        InputStream blankPage = null;
        if (_fallbackDataProvider != null) {
            blankPage = _fallbackDataProvider.getBlankImage();
        }
        if (blankPage == null) {
            return null;
        }
        transCoder.addImage(blankPage);
        int imageNo = transCoder.getImageCount() - 1;
        return transCoder.getImage(imageNo);
    }

    private InputStream getContent(IDfTypedObject row) throws DfException {
        IDfId objectId = row.getId(DocumentumConstants.R_OBJECT_ID);
        if (objectId == null || !objectId.isObjectId()) {
            return null;
        }
        return getContent(row.getSession(), objectId);
    }

    @ThreadLocalCache
    private InputStream getContent(IDfSession session, IDfId objectId)
        throws DfException {
        IDfPersistentObject object = session.getObject(objectId);
        if (!(object instanceof IDfSysObject)) {
            return null;
        }
        IDfSysObject sysObject = (IDfSysObject) object;
        if (sysObject.getContentSize() <= 0) {
            return null;
        }
        return new ByteArrayInputStreamWrapper(sysObject.getContent());
    }

    private Image makeImage(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        Base64InputStream base64Stream = null;
        try {
            Image image = new Image().withEncodeType(EncodeType.BASE_64);
            image.setOriginalLen(inputStream.available());
            base64Stream = new Base64InputStream(Base64InputStream.ENCODE,
                    inputStream);
            StringBuilder stringBuilder = new StringBuilder(inputStream
                    .available() * 4 / 3);
            for (String line : IOUtils.readLines(base64Stream)) {
                stringBuilder.append(line).append('\n');
            }
            image.setEncodedLen(stringBuilder.length());
            image.setContent(stringBuilder.toString());
            return image;
        } finally {
            IOUtils.closeQuietly(base64Stream);
        }
    }

}
