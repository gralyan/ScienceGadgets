package com.sciencegadgets.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.appengine.api.blobstore.BlobInfo;

/**
 * This servlet is mapped to the callback URL used when generating the Blobstore upload URL. 
 * It Processes uploaded Blobs, adds them to the FileCollection, and notifies all clients of 
 * the change. 
 */
@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {
        
        private static Logger log = Logger.getLogger(UploadServlet.class.getName());
        
        public void doPost(HttpServletRequest req, HttpServletResponse res)
                        throws ServletException, IOException {
        	
                List<String> uploadedKeys = BlobstoreUtil.processRequest(req);
                if (uploadedKeys.size() > 0) {
                        for (String b : uploadedKeys) {
                                log.info("Uploaded " + b);
                        }
                } else {
                        log.warning("empty upload");
                }
                
                List<BlobInfo> blobInfos = BlobstoreUtil.loadBlobInfos(uploadedKeys);
                
                // Upload URLs are one time use only, need to send a new URL to the client.
                String newBlobstoreUrl = BlobstoreUtil.getUrl();
                res.setHeader("Content-Type", "text/html"); // Browser will wrap text/plain in <pre> tags
                res.getWriter().print(newBlobstoreUrl);
                res.getWriter().flush();
                res.getWriter().close();
                log.info("Returning new blobstore URL " + newBlobstoreUrl);
        }

}
