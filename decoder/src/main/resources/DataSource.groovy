dataSource {
    driverClassName = 'org.h2.Driver'
    username = 'sa'
    password = ''
    pool {
        idleTimeout = 60000
        maximumPoolSize = 8
        minimumIdle = 5
    }
}

environments {
    development {
        dataSource {
            dbCreate = 'create' // one of ['create', 'skip']
            url = 'jdbc:h2:mem:@application.name@-dev'
        }
    }
    test {
        dataSource {
            dbCreate = 'create'
            url = 'jdbc:h2:mem:@application.name@-test'
        }
    }
    production {
        dataSource {
            dbCreate = 'skip'
            url = 'jdbc:h2:mem:@application.name@-prod'
        }
    }
}