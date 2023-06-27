application {
    title = 'app'
    startupGroups = ['app']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "app"
    'app' {
        model      = 'com.spw.rr.AppModel'
        view       = 'com.spw.rr.AppView'
        controller = 'com.spw.rr.AppController'
    }
}