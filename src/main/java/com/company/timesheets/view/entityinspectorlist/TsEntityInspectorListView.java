package com.company.timesheets.view.entityinspectorlist;

import com.vaadin.flow.router.Route;
import io.jmix.datatoolsflowui.view.entityinspector.EntityInspectorListView;
import io.jmix.flowui.view.DefaultMainViewParent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "datatl/entityinspector", layout = DefaultMainViewParent.class)
@ViewController(id = "datatl_entityInspectorListView")
@ViewDescriptor(path = "ts-entity-inspector-list-view.xml")
public class TsEntityInspectorListView extends EntityInspectorListView {
}