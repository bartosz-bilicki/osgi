package pl.bb.osgi.mybundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import pl.bb.osgi.mybundle.service.DummyService;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceRegistration;

    @Override
    public void start(BundleContext context) throws Exception {
        DummyService service= new DummyService();
        serviceRegistration = context.registerService(DummyService.class.getName(), service, null);
        System.out.println("start");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        serviceRegistration.unregister();
        System.out.println("stop");
    }
}
