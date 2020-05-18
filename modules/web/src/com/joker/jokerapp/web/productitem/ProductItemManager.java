package com.joker.jokerapp.web.productitem;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.joker.jokerapp.entity.ProductItem;

import javax.inject.Inject;
import java.io.File;

@UiController("jokerapp$Product Item Manager")
@UiDescriptor("product-item-manager.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class ProductItemManager extends MasterDetailScreen<ProductItem> {

    @Inject
    protected GroupTable<ProductItem> table;

    @Inject
    protected UiComponents uiComponents;

    @Inject
    protected Form form;

    @Inject
    protected InstanceContainer<ProductItem> productItemsDc;

    @Subscribe
    protected void onInit(InitEvent event) {
        table.addGeneratedColumn(
                "image", entity -> renderProductImage(entity, "small")
        );
        form.add(renderNoProductImage(), 0, 7);
    }

    @Subscribe("table")
    protected void setProductImageInForm(Table.SelectionEvent<ProductItem> event) {

        if (event.getSource().getSingleSelected()!=null) {
            Component productImage = renderProductImage(event.getSource().getSingleSelected(), "large");
            form.remove(form.getComponent(0,7));
            if (productImage!=null) {
                form.add(productImage, 0, 7);
            } else {
                form.add(renderNoProductImage(), 0, 7);
            }
        }

    }

    protected Component renderNoProductImage() {
        Image image = largeImage();
        image.setSource(FileResource.class).setFile(new File("/home/toma/Desktop/noImg.png"));
        return image;
    }

    protected Component renderProductImage(ProductItem productItem, String dimension) {
        FileDescriptor imageFile = productItem.getImage();
        if (imageFile == null) { return null; }
        Image image;
        if (dimension.equals("small")) image = smallImage();
        else image = largeImage();
        image.setSource(FileDescriptorResource.class).setFileDescriptor(imageFile);
        return image;
    }

    protected Image smallImage() {
        Image image = uiComponents.create(Image.class);
        image.setScaleMode(Image.ScaleMode.CONTAIN);
        image.setHeight("40");
        image.setWidth("40");
        image.setStyleName("product-icon-small");
        return image;
    }

    protected Image largeImage() {
        Image image = uiComponents.create(Image.class);
        image.setScaleMode(Image.ScaleMode.CONTAIN);
        image.setHeight("160");
        image.setWidth("160");
        image.setStyleName("product-icon-large");
        return image;
    }

}