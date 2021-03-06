package com.paremus.example.datanucleus.connector.mongo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.PersistenceManagerFactory;

import org.bndtools.service.endpoint.Endpoint;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.paremus.datanucleus.api.PersistenceManagerFactoryBuilder;

@Component  
public class MongoConnector {
    
    public static final String APP_URI_SCHEME = "mongodb";
    public static final String APP_URI_FILTER = "(uri=" + APP_URI_SCHEME + ":*)";
    
    public static final String DATABASE_NAME = "DataNucleusExample";
    
    private String mongoUriStr;
    private PersistenceManagerFactoryBuilder builder;
    private PersistenceManagerFactory persistenceMgrFactory;
    private ServiceRegistration registration;

    @Reference(target = APP_URI_FILTER)
    public void bindEndpoint(Endpoint ep, Map<String, String> serviceProps) {
        this.mongoUriStr = serviceProps.get(Endpoint.URI);
    }
    
    @Reference
    public void bindModel(PersistenceManagerFactoryBuilder builder) {
        this.builder = builder;
    }
    
    @Activate
    public void activate(BundleContext context) throws Exception {
        URI mongoUri = new URI(mongoUriStr);
        String connectionUrl = buildConnectionURL(mongoUri);

        Map<String,Object> props = new HashMap<String,Object>();
        props.put("javax.jdo.option.ConnectionURL", connectionUrl);
        
        persistenceMgrFactory = builder.createPersistenceManagerFactory(props);
        registration = context.registerService(PersistenceManagerFactory.class.getName(), persistenceMgrFactory, null);
    }

    @Deactivate
    public void deactivate() {
        registration.unregister();
        persistenceMgrFactory.close();
    }

    /**
     * <p>
     * DataNucleus uses a "URL" for the MongoDB connection that is not actually a legal URL, therefore we need to
     * convert the URI received from Discovery. We also need to add the database name, this is currently a hard-coded
     * constant but could be supplied from configuration.
     * </p>
     * <p>
     * For reference the required format is <code>mongodb:[{Host}[:{Port}]][/{DbName}]</code>.
     * </p>
     */
    private String buildConnectionURL(URI mongoUri) {
        String host = mongoUri.getHost();
        int port = mongoUri.getPort();
        
        StringBuilder builder = new StringBuilder();
        builder.append(APP_URI_SCHEME).append(":").append(host);
        if (port != -1)
            builder.append(":").append(port);
        builder.append("/").append(DATABASE_NAME);
        
        return builder.toString();
    }

}
