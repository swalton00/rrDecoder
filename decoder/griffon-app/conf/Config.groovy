application {
    title = 'decoder'
    startupGroups = ['decoder']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "app"
    'decoder' {
        model      = 'com.spw.rr.DecoderModel'
        view       = 'com.spw.rr.DecoderView'
        controller = 'com.spw.rr.DecoderController'
    }
}