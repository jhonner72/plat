package com.fujixerox.aus.repository.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

/**
 * Created by vidyavenugopal on 01/07/15.
 */
public class WorkflowProcessor {

	private DfQuery workFlowQuery;
	
	public WorkflowProcessor() {}
	
	public void trigger(IDfSession session, String processName, Date businessDay) throws DfException, NonRetriableException {

		if (processName == null || processName.equals("")) {
			LogUtil.log("process is empty", LogUtil.ERROR, null);
			throw new DfException("The process name not specified");
		}

		String workflowProcessStr = "dm_process where object_name = '" + processName + "'";
		IDfProcess process = (IDfProcess) session.getObjectByQualification(workflowProcessStr);

		if (process == null) {
			LogUtil.log("process not found!", LogUtil.ERROR, null);
			throw new DfException("startWorkflow - No such process: " + processName);
		}

		try {
			DateFormat dateFormat = new SimpleDateFormat(Constant.DOCUMENTUM_DATETIME_FORMAT);
			String newDateStr = dateFormat.format(businessDay);
			IDfTime processTime = new DfTime(newDateStr, IDfTime.DF_TIME_PATTERN14);

			String queryVoucherObjectID = getQueryForWorkFlow(processName, processTime);
			LogUtil.log("Query for getting Voucher Object ID: " + queryVoucherObjectID, LogUtil.DEBUG, null);
			
			String voucherObjectIDStr = "";

			workFlowQuery = new DfQuery(queryVoucherObjectID);
			
			IDfList attachIds = new DfList();
			IDfCollection collection = workFlowQuery.execute(session, IDfQuery.DF_READ_QUERY);
			
			while (collection.next()) {
				voucherObjectIDStr = collection.getString("fxa_voucher_r_object_id");
				
				LogUtil.log("Voucher Object ID " + voucherObjectIDStr, LogUtil.DEBUG, null);
				attachIds.appendId(new DfId(voucherObjectIDStr));
			}

			IDfWorkflowBuilder workflowBuilder = session.newWorkflowBuilder(process.getObjectId());

			startWorkFlow(workflowBuilder, processName);

			attachPackageInWorkflow(session, workflowBuilder, attachIds);

			} catch (Exception ex) {
				LogUtil.log("Workflow  " + processName + " failed triggering in Documentum", LogUtil.INFO, ex);
				throw new NonRetriableException("Workflow " + processName + " failed triggering in Documentum", ex);
			}

	}

	/**
	 * Attaches the voucher object ids to the workflow
	 *
	 * @param session
	 * @param workflowBuilder
	 * @param attachIds
	 * @throws DfException
	 */
	private void attachPackageInWorkflow(IDfSession session, IDfWorkflowBuilder workflowBuilder, IDfList attachIds) throws DfException {

		IDfList startActivities = workflowBuilder.getStartActivityIds();
		int packageIndex = 0;
		for (int i = 0; i < startActivities.getCount(); i++) {
			IDfActivity activity = (IDfActivity) session.getObject(startActivities.getId(i));
			workflowBuilder.addPackage(activity.getObjectName(), activity.getPortName(packageIndex),
					activity.getPackageName(packageIndex), activity.getPackageType(packageIndex), null, false, attachIds);
		}
	}

	/**
	 * Initiates and starts the workflow
	 *
	 * @param workflowBuilder
	 * @param processName
	 * @throws DfException
	 */
	private void startWorkFlow(IDfWorkflowBuilder workflowBuilder, String processName) throws DfException {
		workflowBuilder.initWorkflow();
		workflowBuilder.getWorkflow().setObjectName(processName);
		workflowBuilder.getWorkflow().setSupervisorName(RepositoryProperties.repository_name);
		if ((workflowBuilder.getStartStatus() != 0) || (!(workflowBuilder.isRunnable()))) {
			DfLogger.warn(this, "startWorkflow - workflow '" + processName + "' is not runnable or StartStauts=0!", null, null);
			throw new DfException("cannot start Workflow!");
		}
		workflowBuilder.runWorkflow();
	}

	/**
	 * Creates the query for getting voucher for triggering each of the workflows
	 * For each of the workflow, the flag names are set in the repository properties
	 *
	 * @param workFlowName
	 * @param businessDay
	 * @return Query
	 */
	private String getQueryForWorkFlow(String workFlowName, IDfTime businessDay) {
		switch(workFlowName) {
			case "WF_Surplus_Suspense_pool" :
				return String.format(DocumentumQuery.GET_VOUCHER_WITH_SURPLUS_FLAG, RepositoryProperties.wf_surplus_suspense_pool, businessDay);

			case "WF_ThirdParty_Suspense_Pool" :
				return String.format(DocumentumQuery.GET_VOUCHER_WITH_SUSPENCE_FLAG, RepositoryProperties.wf_thirdparty_suspense_pool, businessDay);

			case "WF_Post_Transmission_QA" :
				return String.format(DocumentumQuery.GET_VOUCHER_WITH_POST_TRANSMISSION_FLAG, RepositoryProperties.wf_post_transmission_qa[0],
						RepositoryProperties.wf_post_transmission_qa[1], businessDay);

			default:
				return null;
		}
	}
}