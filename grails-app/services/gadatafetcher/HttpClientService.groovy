package gadatafetcher

import grails.gorm.transactions.Transactional
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET

@Transactional
class HttpClientService {

    def callRestfulUrl(url,path,query) {
        def http = new HTTPBuilder(url)
        http.request(GET,JSON) {
            uri.path = path
            uri.query = query
            println(uri)
            response.success = { resp, json -> return json }
            response.failure = { resp ->  return null }
        }
    }
}
