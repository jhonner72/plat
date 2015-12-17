package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.reporting.emailreport.Email;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 22/06/15
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliverReportTransform extends AbstractOutclearingsTransform implements Transformer<Email> {
    @Override
    public Email transform(Job job) {

        Email email = new Email();
//        email.setBody();
//        email.setCc();
//        email.setFrom();
//        email.setSubject();
//        email.setTo();

        return email;
    }
}
