package com.joker.jokerapp.web.productitem;

import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.web.gui.components.WebLookupPickerField;
import com.joker.jokerapp.entity.ProductItem;

import javax.inject.Inject;
import javax.inject.Named;
import javax.print.*;
import java.awt.print.PrinterJob;
import java.util.Map;
import java.util.UUID;

public class ProductItemBrowse extends EntityCombinedScreen {
//
//    @Inject
//    private Metadata metadata;
//
//    @Inject
//    private DataManager dataManager;

    @Inject
    private GroupDatasource<ProductItem, UUID> productItemsDs;

    @Named("table")
    private Table<ProductItem> table;

    @Named("productItemModifierCategoryAssocsBox")
    private GroupBoxLayout productItemModifierCategoryAssocsBox;

    @Override
    protected void initNewItem(Entity item) {
        super.initNewItem(item);

    }


    @Override
    public void init(Map<String, Object> params) {

        super.init(params);



    }

    public void onDuplicateBtnClick() {

        if (table.getSelected().size() > 1 || table.getSingleSelected() == null) {
            return;
        }

        ProductItem itemToDuplicate = table.getSingleSelected();
//        ProductItem newProductItem = metadata.create(ProductItem.class);

//        newProductItem.setCategory(itemToDuplicate.getCategory());
//        newProductItem.setName(itemToDuplicate.getName());
//        newProductItem.setPrice(itemToDuplicate.getPrice());
//        newProductItem.setSortOrder(itemToDuplicate.getSortOrder());
//        newProductItem.setVisible(itemToDuplicate.getVisible());

        Action createAction = table.getAction("create");
        createAction.actionPerform(this);
        int max=0;

        for (ProductItem item: productItemsDs.getItems()) {
            if (item.getCategory().equals(itemToDuplicate.getCategory()) && item.getSortOrder() > max) {
                max = item.getSortOrder();
            }

        }

        max += 10;

        TextField name = (TextField) getComponent("fieldGroup.name");
        name.setValue(itemToDuplicate.getName());
        TextField sortOrder = (TextField) getComponent("fieldGroup.sortOrder");
        sortOrder.setValue(max);
        WebLookupPickerField category = (WebLookupPickerField) getComponent("fieldGroup.category");
        category.setValue(itemToDuplicate.getCategory());
        TextField price = (TextField) getComponent("fieldGroup.price");
        price.setValue(itemToDuplicate.getPrice());
        CheckBox visible = (CheckBox) getComponent("fieldGroup.visible");
        visible.setValue(itemToDuplicate.getVisible());

    }

    @Override
    protected void enableEditControls(boolean creating) {
        super.enableEditControls(creating);
        productItemModifierCategoryAssocsBox.setEnabled(true);
    }

    @Override
    protected void disableEditControls() {
        super.disableEditControls();
        productItemModifierCategoryAssocsBox.setEnabled(false);
    }

    public void onBtnPress() {

        PrintService printService = PrinterJob.lookupPrintServices()[0];
        byte[] arr = new byte[2];
        arr[0] = 0x2;
        arr[1] = 0x2;

        Doc doc = new SimpleDoc(arr, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);

        try {
            printService.createPrintJob().print(doc, null);
        } catch (PrintException e) {
            e.printStackTrace();
        }

    }

}