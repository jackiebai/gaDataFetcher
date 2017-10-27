package gadatafetcher

class BootStrap {

    def gaService = new GaService()
    def init = { servletContext ->gaService.getDeviceInfoData()
    }
    def destroy = {
    }
}
