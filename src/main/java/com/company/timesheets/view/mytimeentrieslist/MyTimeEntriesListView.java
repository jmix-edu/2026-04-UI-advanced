package com.company.timesheets.view.mytimeentrieslist;


import com.company.timesheets.app.TimeEntrySupport;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.view.main.MainView;
import com.company.timesheets.view.timeentry.TimeEntryDetailView;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.facet.Timer;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "my-time-entries", layout = MainView.class)
@ViewController(id = "ts_TimeEntries.my")
@ViewDescriptor(path = "my-time-entries-list-view.xml")
public class MyTimeEntriesListView extends StandardView {


    @ViewComponent
    private DataGrid<TimeEntry> timeEntriesDataGrid;
    @Autowired
    private TimeEntrySupport timeEntrySupport;
    @Autowired
    private DialogWindows dialogWindows;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private Timer timer2;

    @Subscribe("timeEntriesDataGrid.copy")
    public void onTimeEntriesDataGridCopy(final ActionPerformedEvent event) {
        TimeEntry selected = timeEntriesDataGrid.getSingleSelectedItem();
        if (selected == null) {
            return;
        }

        TimeEntry copiedEntry = timeEntrySupport.copy(selected);

        DialogWindow<TimeEntryDetailView> window = dialogWindows.detail(timeEntriesDataGrid)
                .withViewClass(TimeEntryDetailView.class)
                .newEntity(copiedEntry)
                .build();

        window.getView().setOwnTimeEntry(true);
        window.open();
    }

    @Install(to = "timeEntriesDataGrid.create", subject = "queryParametersProvider")
    private QueryParameters timeEntriesDataGridCreateQueryParametersProvider() {
        return QueryParameters.of(TimeEntryDetailView.PARAMETER_OWN_TIME_ENTRY, "");
    }

    @Install(to = "timeEntriesDataGrid.edit", subject = "queryParametersProvider")
    private QueryParameters timeEntriesDataGridEditQueryParametersProvider() {
        return QueryParameters.of(TimeEntryDetailView.PARAMETER_OWN_TIME_ENTRY, "");
    }

//    private int seconds = 0;
//
//    @Subscribe("timer1")
//    public void onTimer1TimerAction(final Timer.TimerActionEvent event) {
//        seconds += event.getSource().getDelay() / 1000;
//        notifications.show("Timer tick", seconds + " seconds passed");
//
//    }
//
//    @Subscribe(id = "stopSecondTimerBtn", subject = "clickListener")
//    public void onStopSecondTimerBtnClick(final ClickEvent<JmixButton> event) {
//        timer2.stop();
//    }
//
//    @Subscribe("timer2")
//    public void onTimer2TimerStop(final Timer.TimerStopEvent event) {
//        notifications.create("Timer stopped before (or after) five seconds passed!!")
//                .withPosition(Notification.Position.TOP_END)
//                .withCloseable(true)
//                .withDuration(0)
//                .show();
//
//    }


}