package au.coaas.csi.utils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author ali
 */
public class HttpResponseFuture implements Callback {
    public final CompletableFuture<Response> future = new CompletableFuture<>();

    public HttpResponseFuture() {
    }

    @Override public void onFailure(Call call, IOException e) {
        future.completeExceptionally(e);
    }

    @Override public void onResponse(Call call, Response response) throws IOException {
        future.complete(response);
    }
}
