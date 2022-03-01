package lu.btsin.bobtis;

import org.json.JSONObject;

public class ServerResponse {
    protected API.APIEndpoint endpoint;
    protected int status;
    protected JSONObject response;

    public ServerResponse(API.APIEndpoint endpoint, int status, JSONObject jsonObject) {
        this.endpoint = endpoint;
        this.status = status;
        this.response = jsonObject;
    }
}