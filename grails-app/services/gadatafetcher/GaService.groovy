package gadatafetcher

import grails.gorm.transactions.Transactional
import grails.core.GrailsApplication
import org.apache.juli.logging.LogFactory
import com.xlson.groovycsv.CsvParser

@Transactional
class GaService {

    private static final logger = LogFactory.getLog(this)
    GrailsApplication grailsApplication
    def httpClientService

    def getData(appId) {
        def accessToken = grailsApplication.config.getProperty('ga.accessToken')
        def query = [ids: "ga:$appId", 'start-date': "365daysAgo", 'end-date': "yesterday", metrics: "ga:users", dimensions: "ga:operatingSystem,ga:operatingSystemVersion,ga:mobileDeviceInfo", access_token: accessToken]
        def json = makeGaRequest(query)
        try {
            if (json.rows?.size() > 0) {//if we have rows and there are more than 0 of them
                for (def row : json.rows) {
                    def deviceOsUsers = new DeviceOsUsers(appId: appId, deviceType: row[0], osVersion: row[1], deviceInfo: row[2], totalUserCount: row[3])
                    deviceOsUsers.save()
                }
            }
        }
        catch(Exception ex){
            logger.error("Failed to get data for$appId");
        }
    }

    def makeGaRequest(query){
        def url = grailsApplication.config.getProperty('ga.url') //this is defined in application.properties
        def path = grailsApplication.config.getProperty('ga.path')
        try{
            return httpClientService.callRestfulUrl(url, path, query)
        }
        catch(Exception ex){
            logger.error("GA Fail: $ex.message")
        }
    }

    def getDeviceInfoData(){
        def file = grailsApplication.config.getProperty('ga.file')
        for (def line:CsvParser.parseCsv(new FileReader(file))){getData(line.GAID)}
    }
}
