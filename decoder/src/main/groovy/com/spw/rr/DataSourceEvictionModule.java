package com.spw.rr;

import griffon.core.Configuration;
import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.inject.Evicts;
import griffon.plugins.datasource.DataSourceFactory;
import griffon.plugins.datasource.DataSourceHandler;
import griffon.plugins.datasource.DataSourceStorage;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.datasource.DataSourceAddon;
import org.codehaus.griffon.runtime.datasource.DefaultDataSourceFactory;
import org.codehaus.griffon.runtime.datasource.DefaultDataSourceHandler;
import org.codehaus.griffon.runtime.datasource.DefaultDataSourceStorage;
import org.codehaus.griffon.runtime.util.ResourceBundleProvider;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;
import java.util.ResourceBundle;

import static griffon.util.AnnotationUtils.named;

@Named("datasource")
@Evicts("datasource")
@ServiceProviderFor(Module.class)
public class DataSourceEvictionModule extends AbstractModule {

    @Override
    protected void doConfigure() {
        bind(ResourceBundle.class)
                .withClassifier(named("datasource"))
                .toProvider(new ResourceBundleProvider("DataSource"))
                .asSingleton();

        bind(Configuration.class)
                .withClassifier(named("datasource"))
                .to(MutableDataSourceConfiguration.class)
                .asSingleton();
       bind(DataSourceStorage.class)
                .to(DefaultDataSourceStorage.class)
                .asSingleton();

        bind(DataSourceFactory.class)
                .to(DefaultDataSourceFactory.class)
                .asSingleton();

        bind(DataSourceHandler.class)
                .to(DefaultDataSourceHandler.class)
                .asSingleton();

        bind(GriffonAddon.class)
                .to(DataSourceAddon.class)
                .asSingleton();
    }
}
