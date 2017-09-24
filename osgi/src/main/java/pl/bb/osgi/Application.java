package pl.bb.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class Application {
    private static Logger LOG=LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws BundleException {
        /**
         * I would love to do logging but
         * https://felix.apache.org/documentation/subprojects/apache-felix-config-admin.html#logging
         * Logging goes to the OSGi LogService if such a service is registered int the OSGi framework.
         * If no OSGi LogService is registered,
         * the log output is directed to the Java platform standard error output (System.err).
         */
        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
        Map<String, String> configuration = new HashMap<>();
        configuration.put("felix.log.level","4");
        configuration.put("felix.cm.log.level","4");
        configuration.put("felix.auto.deploy.dir","build/osgi/bundle/");
        configuration.put("felix.auto.deploy.action","start");
        //configuration.put("felix.log.logger",LOG);
        Framework framework = frameworkFactory.newFramework(configuration);
        LOG.info("before start");
        framework.init((FrameworkListener) x -> System.err.println("framework event "+x));
        framework.getBundleContext().addBundleListener(x -> System.err.println("budnle listner "+x));
        framework.start();

        for (Bundle b : framework.getBundleContext().getBundles()) {
            System.err.println(b.getSymbolicName());
        }
        LOG.info("after start");



        /*
        I do not really want to load bundles from code.
        I want to load them as gradle dependencies.
        But I do not want to add them to classpath
         */

        /**
         * now I want to:
         * load my-bundle
         * call myService from my-bundle
         * I must share interface between framework and my-bundle
         *
         * Should I define interface as a interface-bundle?
         * and load interface-bundle into my-bundle ?
         * https://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework-launching-and-embedding.html#providing-host-application-services
         *
         *  host must define a bundle with stuff he want to share
         *  and expose it using org.osgi.framework.system.packages.extra
         *
         *  then use https://osgi.org/javadoc/r4v42/org/osgi/util/tracker/ServiceTracker.html
         *  (inside application) to get instance of registered module)
         */

        /*
        what else we want to share from container to plugin(bundle)?

        logger
            logger is plugin loaded by container
            as a bundle, loaded by plugin bundle!

        configuration
            loaded by container. I believe it is wrong to let plugin load configuration
            it makes configuration management messy and unclear. remember we will have to do deployments!
            do we really want to support all possible ways to deliver configuration
             * enviroment properties (unstructured names, conflicts!)
             * files (maybe from absolute hardcoded paths?)
             * url (hardcoded)
             *
            instead we should support
            * prefix per plugin
            * environment variables/instance variables/property files only
         */

    }
}
