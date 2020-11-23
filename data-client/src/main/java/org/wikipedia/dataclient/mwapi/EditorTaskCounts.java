package org.wikipedia.dataclient.mwapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import org.wikipedia.json.GsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class EditorTaskCounts {
    @Nullable private JsonElement counts;
    @Nullable @SerializedName("targets_passed") private JsonElement targetsPassed;
    @Nullable private JsonElement targets;

    @NonNull
    public Map<String, Integer> getEditsPerLanguage(String mode) {
        Map<String, Integer> editsPerLanguage = null;
        if (counts != null && !(counts instanceof JsonArray)) {
            if (mode == "caption"){
                editsPerLanguage = GsonUtil.getDefaultGson().fromJson(counts, Counts.class).appCaptionEdits;
            } else if (mode == "description"){
                editsPerLanguage = GsonUtil.getDefaultGson().fromJson(counts, Counts.class).appDescriptionEdits;
            }
        }
        return editsPerLanguage == null ? Collections.emptyMap() : editsPerLanguage;
    }

    public int getEditsTargetsPassedCount(String mode) {
        List<Integer> targetList = getEdits(false, mode);
        List<Integer> passedList = getEdits(true, mode);
        int count = 0;
        if (!targetList.isEmpty() && !passedList.isEmpty()) {
            for (int target : targetList) {
                if (passedList.contains(target)) {
                    count++;
                }
            }
        }
        return count;
    }

    @NonNull
    public List<Integer> getEdits(Boolean passed, String mode) {
        List<Integer> list = null;
        JsonElement selectedTarget = passed ? this.targetsPassed : this.targets;
        if (selectedTarget != null && !(selectedTarget instanceof JsonArray)) {
            if (mode == "description") {
                list = GsonUtil.getDefaultGson().fromJson(selectedTarget, Targets.class).appDescriptionEdits;
            } else if (mode == "caption") {
                list = GsonUtil.getDefaultGson().fromJson(selectedTarget, Targets.class).appCaptionEdits;
            }
        }
        return list == null ? Collections.emptyList() : list;
    }

    public class Counts {
        @Nullable @SerializedName("app_description_edits") private Map<String, Integer> appDescriptionEdits;
        @Nullable @SerializedName("app_caption_edits") private Map<String, Integer> appCaptionEdits;
    }

    public class Targets {
        @Nullable @SerializedName("app_description_edits") private List<Integer> appDescriptionEdits;
        @Nullable @SerializedName("app_caption_edits") private List<Integer> appCaptionEdits;
    }
}
