package org.tokenizer.core.jpa;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.user.dao.UserDao;
import org.xaloon.core.api.user.model.User;
import org.xaloon.core.jpa.user.JpaUserDao;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class Test {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);
    private final UserDao userDao;
    protected static Injector injector;
    final PersistService service;

    @Inject
    Test(final PersistService service) {
        this.service = service;

        service.start();
        this.userDao = injector.getInstance(JpaUserDao.class);
        // At this point JPA is started and ready.
    }


    public static void main(final String[] args) {

        LOGGER.warn("starting...");

        initInjector();

        final Test t = injector.getInstance(Test.class);

        User u = t.userDao.newUser();

        u.setFirstName("Fuad ZZZ 2 333 555");
        u.setLastName("Efendi");
        u.setEmail("fuad555@efendi.ca");
        u.setUsername("username 555");
		
        t.userDao.save(u);

        // register for close
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                t.service.stop();
            }
        });


        LOGGER.warn("finishing...");

    }

    protected static void initInjector() {
        injector = Guice.createInjector(new JpaPersistModule("default-persistence-unit"));
        injector = injector.createChildInjector(getModules());
    }

    private static List<Module> getModules() {
        List<Module> baseModules = new ArrayList<>();

        baseModules.add(new PersistenceServicesModule());

        return baseModules;
    }

}
