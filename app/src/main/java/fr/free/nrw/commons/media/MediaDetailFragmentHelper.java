package fr.free.nrw.commons.media;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static fr.free.nrw.commons.category.CategoryClientKt.CATEGORY_NEEDING_CATEGORIES;
import static fr.free.nrw.commons.category.CategoryClientKt.CATEGORY_PREFIX;
import static fr.free.nrw.commons.category.CategoryClientKt.CATEGORY_UNCATEGORISED;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSearchView;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.category.CategoryDetailsActivity;
import fr.free.nrw.commons.category.CategoryEditHelper;
import fr.free.nrw.commons.category.CategoryEditSearchRecyclerViewAdapter;
import fr.free.nrw.commons.category.CategoryEditSearchRecyclerViewAdapter.Callback;
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment;
import fr.free.nrw.commons.explore.depictions.WikidataItemDetailsActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.wikipedia.util.DateUtil;
import timber.log.Timber;

public class MediaDetailFragmentHelper extends CommonsDaggerSupportFragment implements MediaDetailFragmentHelperInterface {

  //As per issue #1826(see https://github.com/commons-app/apps-android-commons/issues/1826), some categories come suffixed with strings prefixed with |. As per the discussion
  //that was meant for alphabetical sorting of the categories and can be safely removed.
  public String sanitise(String category) {
    int indexOfPipe = category.indexOf('|');
    if (indexOfPipe != -1) {
      //Removed everything after '|'
      return category.substring(0, indexOfPipe);
    }
    return category;
  }
  /**
   * Add view to depictions obtained also tapping on depictions should open the url
   */
  public View buildDepictLabel(String depictionName, String entityId, LinearLayout depictionContainer) {
    final View item = LayoutInflater
        .from(getContext()).inflate(R.layout.detail_category_item, depictionContainer,false);
    final TextView textView = item.findViewById(R.id.mediaDetailCategoryItemText);
    textView.setText(depictionName);
    item.setOnClickListener(view -> {
      Intent intent = new Intent(getContext(), WikidataItemDetailsActivity.class);
      intent.putExtra("wikidataItemName", depictionName);
      intent.putExtra("entityId", entityId);
      getContext().startActivity(intent);
    });
    return item;
  }
  public View buildCatLabel(final String catName, ViewGroup categoryContainer) {
    final View item = LayoutInflater.from(getContext()).inflate(R.layout.detail_category_item, categoryContainer, false);
    final TextView textView = item.findViewById(R.id.mediaDetailCategoryItemText);

    textView.setText(catName);
    if(!getString(R.string.detail_panel_cats_none).equals(catName)) {
      textView.setOnClickListener(view -> {
        // Open Category Details page
        String selectedCategoryTitle = CATEGORY_PREFIX + catName;
        Intent intent = new Intent(getContext(), CategoryDetailsActivity.class);
        intent.putExtra("categoryName", selectedCategoryTitle);
        getContext().startActivity(intent);
      });
    }
    return item;
  }
  /**
   * Returns captions for media details
   *
   * @param media object of class media
   * @return caption as string
   */
  @Override
  public String prettyCaption(Media media) {
    for (String caption : media.getCaptions().values()) {
      if (caption.equals("")) {
        return getString(R.string.detail_caption_empty);
      } else {
        return caption;
      }
    }
    return getString(R.string.detail_caption_empty);
  }

  @Override
  public String prettyDescription(Media media) {
    final String description = chooseDescription(media);
    return description.isEmpty() ? getString(R.string.detail_description_empty)
        : description;
  }

  @Override
  public String chooseDescription(Media media) {
    final Map<String, String> descriptions = media.getDescriptions();
    final String multilingualDesc = descriptions.get(Locale.getDefault().getLanguage());
    if (multilingualDesc != null) {
      return multilingualDesc;
    }
    for (String description : descriptions.values()) {
      return description;
    }
    return media.getFallbackDescription();
  }

  public String prettyDiscussion(String discussion) {
    return discussion.isEmpty() ? getString(R.string.detail_discussion_empty) : discussion;
  }

  @Override
  public String prettyLicense(Media media) {
    String licenseKey = media.getLicense();
    Timber.d("Media license is: %s", licenseKey);
    if (licenseKey == null || licenseKey.equals("")) {
      return getString(R.string.detail_license_empty);
    }
    return licenseKey;
  }

  @Override
  public String prettyUploadedDate(Media media) {
    Date date = media.getDateUploaded();
    if (date == null || date.toString() == null || date.toString().isEmpty()) {
      return "Uploaded date not available";
    }
    return DateUtil.getDateStringWithSkeletonPattern(date, "dd MMM yyyy");
  }

  /**
   * Returns the coordinates nicely formatted.
   *
   * @return Coordinates as text.
   */
  @Override
  public String prettyCoordinates(Media media) {
    if (media.getCoordinates() == null) {
      return getString(R.string.media_detail_coordinates_empty);
    }
    return media.getCoordinates().getPrettyCoordinateString();
  }
}
