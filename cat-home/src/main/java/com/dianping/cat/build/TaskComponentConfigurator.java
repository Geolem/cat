package com.dianping.cat.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.cat.DomainManager;
import com.dianping.cat.ServerConfigManager;
import com.dianping.cat.consumer.metric.MetricConfigManager;
import com.dianping.cat.consumer.metric.ProductLineConfigManager;
import com.dianping.cat.core.dal.DailyGraphDao;
import com.dianping.cat.core.dal.GraphDao;
import com.dianping.cat.core.dal.TaskDao;
import com.dianping.cat.home.dal.report.BaselineDao;
import com.dianping.cat.home.dal.report.TopologyGraphDao;
import com.dianping.cat.report.baseline.BaselineConfigManager;
import com.dianping.cat.report.baseline.BaselineCreator;
import com.dianping.cat.report.baseline.BaselineService;
import com.dianping.cat.report.baseline.impl.DefaultBaselineCreator;
import com.dianping.cat.report.baseline.impl.DefaultBaselineService;
import com.dianping.cat.report.page.dependency.graph.TopologyGraphBuilder;
import com.dianping.cat.report.page.network.nettopology.NetGraphBuilder;
import com.dianping.cat.report.page.transaction.TransactionMergeManager;
import com.dianping.cat.report.service.ReportServiceManager;
import com.dianping.cat.report.task.DefaultTaskConsumer;
import com.dianping.cat.report.task.alert.exception.AlertReportBuilder;
import com.dianping.cat.report.task.bug.BugReportBuilder;
import com.dianping.cat.report.task.cross.CrossReportBuilder;
import com.dianping.cat.report.task.dependency.DependencyReportBuilder;
import com.dianping.cat.report.task.event.EventGraphCreator;
import com.dianping.cat.report.task.event.EventMerger;
import com.dianping.cat.report.task.event.EventReportBuilder;
import com.dianping.cat.report.task.heartbeat.HeartbeatGraphCreator;
import com.dianping.cat.report.task.heartbeat.HeartbeatReportBuilder;
import com.dianping.cat.report.task.heavy.HeavyReportBuilder;
import com.dianping.cat.report.task.matrix.MatrixReportBuilder;
import com.dianping.cat.report.task.metric.MetricBaselineReportBuilder;
import com.dianping.cat.report.task.metric.MetricPointParser;
import com.dianping.cat.report.task.network.NetTopologyReportBuilder;
import com.dianping.cat.report.task.problem.ProblemGraphCreator;
import com.dianping.cat.report.task.problem.ProblemMerger;
import com.dianping.cat.report.task.problem.ProblemReportBuilder;
import com.dianping.cat.report.task.router.RouterConfigBuilder;
import com.dianping.cat.report.task.service.ServiceReportBuilder;
import com.dianping.cat.report.task.spi.ReportFacade;
import com.dianping.cat.report.task.spi.ReportTaskBuilder;
import com.dianping.cat.report.task.state.StateReportBuilder;
import com.dianping.cat.report.task.transaction.TransactionGraphCreator;
import com.dianping.cat.report.task.transaction.TransactionMerger;
import com.dianping.cat.report.task.transaction.TransactionReportBuilder;
import com.dianping.cat.report.task.utilization.UtilizationReportBuilder;
import com.dianping.cat.system.config.ExceptionConfigManager;
import com.dianping.cat.system.config.NetGraphConfigManager;
import com.dianping.cat.system.config.RouterConfigManager;

public class TaskComponentConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(DefaultTaskConsumer.class) //
		      .req(TaskDao.class, ReportFacade.class));

		all.add(C(TransactionGraphCreator.class));
		all.add(C(EventGraphCreator.class));
		all.add(C(ProblemGraphCreator.class));
		all.add(C(HeartbeatGraphCreator.class));

		all.add(C(TransactionMerger.class));
		all.add(C(EventMerger.class));
		all.add(C(ProblemMerger.class));

		all.add(C(MetricPointParser.class));
		all.add(C(BaselineCreator.class, DefaultBaselineCreator.class));
		all.add(C(BaselineService.class, DefaultBaselineService.class).req(BaselineDao.class));
		all.add(C(BaselineConfigManager.class, BaselineConfigManager.class));

		all.add(C(ReportTaskBuilder.class, MetricBaselineReportBuilder.ID, MetricBaselineReportBuilder.class)
		      .req(ReportServiceManager.class, MetricPointParser.class)//
		      .req(MetricConfigManager.class, ProductLineConfigManager.class)//
		      .req(BaselineCreator.class, BaselineService.class, BaselineConfigManager.class));

		all.add(C(ReportTaskBuilder.class, TransactionReportBuilder.ID, TransactionReportBuilder.class) //
		      .req(GraphDao.class, DailyGraphDao.class, ReportServiceManager.class)//
		      .req(TransactionGraphCreator.class, TransactionMerger.class));

		all.add(C(ReportTaskBuilder.class, EventReportBuilder.ID, EventReportBuilder.class) //
		      .req(GraphDao.class, DailyGraphDao.class, ReportServiceManager.class)//
		      .req(EventGraphCreator.class, EventMerger.class));//

		all.add(C(ReportTaskBuilder.class, ProblemReportBuilder.ID, ProblemReportBuilder.class) //
		      .req(GraphDao.class, DailyGraphDao.class, ReportServiceManager.class)//
		      .req(ProblemGraphCreator.class, ProblemMerger.class));

		all.add(C(ReportTaskBuilder.class, HeartbeatReportBuilder.ID, HeartbeatReportBuilder.class) //
		      .req(GraphDao.class, ReportServiceManager.class) //
		      .req(HeartbeatGraphCreator.class));

		all.add(C(ReportTaskBuilder.class, BugReportBuilder.ID, BugReportBuilder.class).req(ReportServiceManager.class));

		all.add(C(ReportTaskBuilder.class, ServiceReportBuilder.ID, ServiceReportBuilder.class).req(ReportServiceManager.class,
		      DomainManager.class));

		all.add(C(ReportTaskBuilder.class, MatrixReportBuilder.ID, MatrixReportBuilder.class).req(ReportServiceManager.class));

		all.add(C(ReportTaskBuilder.class, CrossReportBuilder.ID, CrossReportBuilder.class).req(ReportServiceManager.class));

		all.add(C(ReportTaskBuilder.class, StateReportBuilder.ID, StateReportBuilder.class).req(ReportServiceManager.class));

		all.add(C(ReportTaskBuilder.class, RouterConfigBuilder.ID, RouterConfigBuilder.class).req(ReportServiceManager.class,
		      RouterConfigManager.class));

		all.add(C(ReportTaskBuilder.class, AlertReportBuilder.ID, AlertReportBuilder.class).req(ReportServiceManager.class,
		      ExceptionConfigManager.class));

		all.add(C(ReportTaskBuilder.class, HeavyReportBuilder.ID, HeavyReportBuilder.class).req(ReportServiceManager.class));

		all.add(C(ReportTaskBuilder.class, UtilizationReportBuilder.ID, UtilizationReportBuilder.class).req(
		      ReportServiceManager.class, TransactionMergeManager.class, ServerConfigManager.class, DomainManager.class));

		all.add(C(ReportTaskBuilder.class, DependencyReportBuilder.ID, DependencyReportBuilder.class).req(
		      ReportServiceManager.class, TopologyGraphBuilder.class, TopologyGraphDao.class));

		all.add(C(ReportTaskBuilder.class, NetTopologyReportBuilder.ID, NetTopologyReportBuilder.class).req(
		      ReportServiceManager.class, NetGraphBuilder.class, NetGraphConfigManager.class));

		all.add(C(ReportFacade.class));

		return all;
	}
}
