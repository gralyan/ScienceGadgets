package com.sciencegadgets.client;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.entities.DataModerator;

public class UploadButton extends Composite {
        
        private FlowPanel contentPanel = new FlowPanel();
        private FormPanel shadowForm =  new FormPanel();
        private FileUpload shadowUpload = new FileUpload();
        private Button uploadFace = new Button("Upload");
        private UploadButton uploadButton = this;
                        
        // ========================================================================= //
        //                      INITILIZING                                                                                                              //
        // ========================================================================= //
        
        public UploadButton() {
        	uploadButton.setEncoding(FormPanel.ENCODING_MULTIPART);
            uploadButton.setMethod(FormPanel.METHOD_POST);
        	DataModerator.database.getBlobURL(new AsyncCallback<String>() {
				@Override
				public void onSuccess(String url) {
					uploadButton.setAction(url);
				}
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Upload failed :(");
				}
			});
        	
                initWidget(contentPanel);
                
                shadowUpload.getElement().getStyle().setDisplay(Display.NONE);
                shadowUpload.setName("upload");
                shadowUpload.getElement().setAttribute("multiple", "multiple");  
                shadowUpload.addChangeHandler(new ChangeHandler() {
                        
                        @Override
                        public void onChange(ChangeEvent event) {
                                shadowForm.submit();
                                shadowForm.reset();
                        }
                });

                shadowForm.addSubmitHandler(new SubmitHandler() {

                        @Override
                        public void onSubmit(SubmitEvent event) {
//                                setLoadingFace();
                        }
                });

                shadowForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {

                        @Override
                        public void onSubmitComplete(SubmitCompleteEvent event) {
                                String newUrl = event.getResults();
                                setAction(newUrl);
                        }
                });
                
                // Add the visible button
                uploadFace.addDomHandler(new ClickHandler() {
                        
                        @Override
                        public void onClick(ClickEvent event) {
                                shadowUpload.getElement().<InputElement>cast().click();
                                
                                uploadFace.addStyleName("shakes");
                                
                                new Timer() {

                                        @Override
                                        public void run() {
                                                uploadFace.removeStyleName("shakes");
                                        }
                                        
                                }.schedule(700);
                                
                        }
                }, ClickEvent.getType());
                
                shadowForm.add(shadowUpload);
                contentPanel.add(uploadFace);   
                contentPanel.add(shadowForm);
        }

        // ========================================================================= //
        //                      EXPOSED FORM METHODS                                                                                     //
        // ========================================================================= //

        public void setAction(String s) {
                shadowForm.setAction(s);
        }
        
        public void setEncoding(String s) {
                shadowForm.setEncoding(s);
        }
        
        public void setMethod(String s){
                shadowForm.setMethod(s);
        }
        
        public void addSubmitHandler(SubmitHandler h) {
                shadowForm.addSubmitHandler(h);
        }
        
        public void addSubmitCompleteHandler(SubmitCompleteHandler h) {
                shadowForm.addSubmitCompleteHandler(h);
        }
        
}
