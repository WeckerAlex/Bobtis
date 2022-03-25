package lu.btsin.bobtis;

public class ServerResponse {
    protected API.APIEndpoint endpoint;
    protected int status;
    protected String response;

    public ServerResponse(API.APIEndpoint endpoint, int status, String jsonObject) {
        this.endpoint = endpoint;
        this.status = status;
        this.response = jsonObject;
    }
}