package net.troja.eve.pve;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.TypeResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiUtils {
    public static String getETag(ApiResponse<TypeResponse> resp) {
        return resp.getHeaders().get("etag").getFirst();
    }

    public static Integer getPagesMax(ApiResponse<?> resp) {
        return Integer.valueOf(resp.getHeaders().get("X-Pages").getFirst());
    }
}
