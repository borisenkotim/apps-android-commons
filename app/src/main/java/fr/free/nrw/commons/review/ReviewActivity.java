package fr.free.nrw.commons.review;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;
import com.viewpagerindicator.CirclePageIndicator;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.delete.DeleteHelper;
import fr.free.nrw.commons.theme.NavigationBaseActivity;
import fr.free.nrw.commons.utils.DialogUtil;
import fr.free.nrw.commons.utils.ViewUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ReviewActivity extends NavigationBaseActivity {

    @BindView(R.id.pager_indicator_review)
    public CirclePageIndicator pagerIndicator;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.view_pager_review)
    ReviewViewPager reviewPager;
    @BindView(R.id.skip_image)
    Button btnSkipImage;
    @BindView(R.id.review_image_view)
    SimpleDraweeView simpleDraweeView;
    @BindView(R.id.pb_review_image)
    ProgressBar progressBar;
    @BindView(R.id.tv_image_caption)
    TextView imageCaption;
    public ReviewPagerAdapter reviewPagerAdapter;
    public ReviewController reviewController;
    @Inject
    ReviewHelper reviewHelper;
    @Inject
    DeleteHelper deleteHelper;
    public static final String PREFS_NAME = "MyPrefsFile";
    final String SAVED_MEDIA = "saved_media";
    private Media media;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (media != null) {
            outState.putParcelable(SAVED_MEDIA, media);
        }
    }

    /**
     * Consumers should be simply using this method to use this activity.
     *
     * @param context
     * @param title   Page title
     */
    public static void startYourself(Context context, String title) {
        Intent reviewActivity = new Intent(context, ReviewActivity.class);
        reviewActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        reviewActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(reviewActivity);
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public Media getMedia() {
        return media;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initDrawer();

        reviewController = new ReviewController(deleteHelper, this);

        reviewPagerAdapter = new ReviewPagerAdapter(getSupportFragmentManager());
        reviewPager.setAdapter(reviewPagerAdapter);
        reviewPagerAdapter.getItem(0);
        pagerIndicator.setViewPager(reviewPager);
        progressBar.setVisibility(View.VISIBLE);

        Drawable d[] = btnSkipImage.getCompoundDrawablesRelative();
        d[2].setColorFilter(getApplicationContext().getResources().getColor(R.color.button_blue),
            PorterDuff.Mode.SRC_IN);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME    , 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            showReviewImageInfo();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.commit();
        }

        if (savedInstanceState != null && savedInstanceState.getParcelable(SAVED_MEDIA) != null) {
            updateImage(
                savedInstanceState.getParcelable(SAVED_MEDIA)); // Use existing media if we have one
        } else {
            runRandomizer(); //Run randomizer whenever everything is ready so that a first random image will be added
        }

        btnSetup();
    }

    private void btnSetup() {
        btnSkipImage.setOnClickListener(view -> {
            reviewPagerAdapter.disableButtons();
            runRandomizer();
        });

        btnSkipImage.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= (
                btnSkipImage.getRight() - btnSkipImage
                    .getCompoundDrawables()[2].getBounds().width())) {
                showSkipImageInfo();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("CheckResult")
    public boolean runRandomizer() {
        progressBar.setVisibility(View.VISIBLE);
        reviewPager.setCurrentItem(0);
        compositeDisposable.add(reviewHelper.getRandomMedia()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(media -> {
                reviewPagerAdapter.disableButtons();
                updateImage(media);
            }));
        return true;
    }

    @SuppressLint("CheckResult")
    private void updateImage(Media media) {
        this.media = media;
        String fileName = media.getFilename();
        if (fileName.length() == 0) {
            ViewUtil.showShortSnackbar(drawerLayout, R.string.error_review);
            return;
        }

        simpleDraweeView.setImageURI(media.getImageUrl());

        reviewController.onImageRefreshed(media); //file name is updated
        compositeDisposable.add(reviewHelper.getFirstRevisionOfFile(fileName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(revision -> {
                reviewController.firstRevision = revision;
                reviewPagerAdapter.updateFileInformation();
                @SuppressLint("StringFormatInvalid") String caption = String.format(getString(R.string.review_is_uploaded_by), fileName,
                    revision.getUser());
                imageCaption.setText(caption);
                progressBar.setVisibility(View.GONE);
                reviewPagerAdapter.enableButtons();
            }));
        reviewPager.setCurrentItem(0);
    }

    public void swipeToNext() {
        int nextPos = reviewPager.getCurrentItem() + 1;
        if (nextPos <= 3) {
            reviewPager.setCurrentItem(nextPos);
        } else {
            runRandomizer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    public void showOutOfScopeInfo() {
        dialogueHelper(getString(R.string.title_out_of_scope_info).toUpperCase(),
            R.string.out_of_scope_explanation);
    }

    private void dialogueHelper(String s, int p) {
        DialogUtil.showAlertDialog(ReviewActivity.this,
            s,
            getString(p),
            getString(android.R.string.ok),
            "",
            null,
            null);
    }

    public void showSkipImageInfo() {
        dialogueHelper(getString(R.string.title_skip_image_info),
            R.string.skipping_image_explanation);
    }

    public void showSeemsFineInfo() {
        dialogueHelper(getString(R.string.title_seems_fine_info), R.string.seems_fine_explanation);
    }

    public void showReviewImageInfo() {
        dialogueHelper(getString(R.string.title_peer_review_information),
            R.string.review_image_explanation);
    }

    public void showInfo(){

        AlertDialog.Builder builder
            = new AlertDialog
            .Builder(ReviewActivity.this);
        builder.setMessage("Click on one of the options for more information about the Wikimedia Commons Peer Review");
        builder.setTitle("Wikimedia Commons Peer Review Help!");
        builder.setCancelable(false);
        builder
            .setPositiveButton(
                "Skip image information",
                (dialog, which) -> showSkipImageInfo());
        builder
            .setNegativeButton(
                "Out of scope information",
                (dialog, which) -> showOutOfScopeInfo());
        builder
            .setNeutralButton(
                "Seems fine information",
                (dialog, which) -> showSeemsFineInfo());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review_activty, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_image_info:
                showInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}