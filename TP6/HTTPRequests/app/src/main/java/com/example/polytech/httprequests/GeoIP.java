package com.example.polytech.httprequests;

public class GeoIP {
    private final String returnCode;
    private final String IP;
    private final String returnCodeDetails;
    private final String countryName;
    private final String countryCode;
    private final String organisation;
    private final String regionName;
    private final String zip;
    private final String ISP;
    private final Double lat;
    private final Double lon;

    public GeoIP(String returnCode, String IP, String returnCodeDetails, String countryName, String countryCode, String organisation, String regionName, String zip, String isp, Double lat, Double lon) {
        this.returnCode = returnCode;
        this.IP = IP;
        this.returnCodeDetails = returnCodeDetails;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.organisation = organisation;
        this.regionName = regionName;
        this.zip = zip;
        ISP = isp;
        this.lat = lat;
        this.lon = lon;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getZip() {
        return zip;
    }

    public String getISP() {
        return ISP;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public String getIP() {
        return IP;
    }

    public String getReturnCodeDetails() {
        return returnCodeDetails;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString() {
        return "GeoIP{" +
                "returnCode='" + returnCode + '\'' +
                ", IP='" + IP + '\'' +
                ", returnCodeDetails='" + returnCodeDetails + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", organisation='" + organisation + '\'' +
                ", regionName='" + regionName + '\'' +
                ", zip='" + zip + '\'' +
                ", ISP='" + ISP + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    public boolean succeeded() {
        return returnCode != null && returnCode.equalsIgnoreCase("success");
    }

    public String forView() {
        StringBuilder sb = new StringBuilder();
        sb.append("Return status: ").append(getReturnCode()).append("\n");
        if(!succeeded())
            sb.append("Failure message: ").append(getReturnCodeDetails()).append("\n");
        sb.append("IP requested: ").append(getIP()).append("\n");
        if(succeeded()){
            sb.append("Country: ").append(getCountryName()).append("\n");
            sb.append("Country code: ").append(getCountryCode()).append("\n");
            sb.append("Organisation: ").append(getOrganisation()).append("\n");
            sb.append("Region: ").append(getRegionName()).append("\n");
            sb.append("Zip code: ").append(getZip()).append("\n");
            sb.append("ISP: ").append(getISP()).append("\n");
            sb.append("Latitude: ").append(getLat()).append("\n");
            sb.append("Longitude: ").append(getLon()).append("\n");
        }
        return sb.toString();
    }
}
