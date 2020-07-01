package com.example.calendarmanagerbeta;

import android.provider.ContactsContract;

import com.google.gson.JsonObject;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.ProfilePhoto;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graph.requests.extensions.ProfilePhotoStreamRequest;
import com.sun.research.ws.wadl.Link;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

// Singleton class - the app only needs a single instance
// of the Graph client
public class GraphHelper implements IAuthenticationProvider {
    private static GraphHelper INSTANCE = null;
    private IGraphServiceClient mClient = null;
    private String mAccessToken = null;

    private GraphHelper() {
        mClient = GraphServiceClient.builder()
                .authenticationProvider(this).buildClient();
    }

    public static synchronized GraphHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphHelper();
        }

        return INSTANCE;
    }

    // Part of the Graph IAuthenticationProvider interface
    // This method is called before sending the HTTP request
    @Override
    public void authenticateRequest(IHttpRequest request) {
        // Add the access token in the Authorization header
        request.addHeader("Authorization", "Bearer " + mAccessToken);
    }

    public void getUser(String accessToken, ICallback<User> callback) {
        mAccessToken = accessToken;

        // GET /me (logged in user)
        mClient.me().buildRequest().get(callback);
    }


    public void getEvents(String accessToken, ICallback<IEventCollectionPage> callback) {
        mAccessToken = accessToken;

        // Use query options to sort by created time
        final List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("orderby", "createdDateTime DESC"));


        // GET /me/events
        mClient.me().events().buildRequest(options)
                .select("subject,organizer,start,end")
                .get(callback);

    }

    public void getEmails(String accessToken, ICallback<IMessageCollectionPage> callback){
        mAccessToken = accessToken;
        mClient.me().mailFolders("inbox").messages().buildRequest().select("sender,subject,bodyPreview,isRead").get(callback);
    }

    public void getSpecificEmails(String accessToken, String moduleCode, ICallback<IMessageCollectionPage> callback){
        mAccessToken = accessToken;
        LinkedList<Option> requestOptions = new LinkedList<Option>();
        String completedRequest = completeRequest(moduleCode);
        requestOptions.add(new QueryOption("orderby", "receivedDateTime%20desc"));
        requestOptions.add(new QueryOption("filter", completedRequest));

        mClient.me().mailFolders("inbox").messages().buildRequest(requestOptions).get(callback);
    }

    // Helper function to complete module email request
    private String completeRequest(String moduleCode){
        return "receivedDateTime ge 1900-01-01T00:00:00Z and contains(subject,'" + moduleCode + "')";
    }


    public void getProfilePicture(String accessToken, final ICallback<InputStream> callback){
        mAccessToken = accessToken;
        mClient.me().photo().content().buildRequest().get(callback);
    }

    // Debug function to get the JSON representation of a Graph
// object
    public String serializeObject(Object object) {
        return mClient.getSerializer().serializeObject(object);
    }
}