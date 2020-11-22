package fr.free.nrw.commons.upload;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import com.google.gson.JsonObject;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static org.wikipedia.dataclient.Service.MW_API_PREFIX;

public interface UploadInterface {

  @Multipart
  @POST(MW_API_PREFIX + "action=upload&stash=1&ignorewarnings=1")
  Observable<UploadResponse> uploadFileToStash(@Part("filename") RequestBody filename,
      @Part("filesize") RequestBody totalFileSize,
      @Part("offset") RequestBody offset,
      @Part("filekey") RequestBody fileKey,
      @Part("token") RequestBody token,
      @Part MultipartBody.Part filePart);

  @Headers("Cache-Control: no-cache")
  @POST(MW_API_PREFIX + "action=upload&ignorewarnings=1")
  @FormUrlEncoded
  @NonNull
  Observable<JsonObject> uploadFileFromStash(@NonNull @Field("token") String token,
      @NonNull @Field("text") String text,
      @NonNull @Field("comment") String comment,
      @NonNull @Field("filename") String filename,
      @NonNull @Field("filekey") String filekey);

  /**
   * The adapter used to show image upload intermediate fragments
   */

  class UploadImageAdapter extends FragmentStatePagerAdapter {
      List<UploadBaseFragment> fragments;

      public UploadImageAdapter(FragmentManager fragmentManager) {
          super(fragmentManager);
          this.fragments = new ArrayList<>();
      }

      public void setFragments(List<UploadBaseFragment> fragments) {
          this.fragments = fragments;
          notifyDataSetChanged();
      }

      @Override
      public Fragment getItem(int position) {
          return fragments.get(position);
      }

      @Override
      public int getCount() {
          return fragments.size();
      }

      @Override
      public int getItemPosition(Object object) {
          return PagerAdapter.POSITION_NONE;
      }
  }
}
