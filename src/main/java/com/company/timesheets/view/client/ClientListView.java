package com.company.timesheets.view.client;

import com.company.timesheets.entity.Client;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "clients", layout = MainView.class)
@ViewController("ts_Client.list")
@ViewDescriptor("client-list-view.xml")
@LookupComponent("clientsDataGrid")
@DialogMode(width = "64em")
//@PrimaryLookupView(Client.class)
public class ClientListView extends StandardListView<Client> {
    @Subscribe
    public void onInit(final InitEvent event) {
        H4 header = new H4("InitEvent");
        getContent().add(header);
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        H4 header = new H4("BeforeShowEvent");
        getContent().add(header);
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        H4 header = new H4("ReadyEvent");
        getContent().add(header);
    }
    
    
}