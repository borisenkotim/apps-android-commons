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

    public int getDescriptionEditTargetsPassedCount() {
        List<Integer> targetList = getDescriptionEdits(false);
        List<Integer> passedList = getDescriptionEdits(true);
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
    public List<Integer> getDescriptionEdits(Boolean passed) {
        List<Integer> list = null;
        JsonElement selectedTarget = passed ? this.targetsPassed : this.targets;
        if (selectedTarget != null && !(selectedTarget instanceof JsonArray)) {
            list = GsonUtil.getDefaultGson().fromJson(selectedTarget, Targets.class).appDescriptionEdits;
        }
        return list == null ? Collections.emptyList() : list;
    }

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

    public int getCaptionEditTargetsPassedCount() {
        List<Integer> targetList = getCaptionEdits(false);
        List<Integer> passedList = getCaptionEdits(true);
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
    public List<Integer> getCaptionEdits(Boolean passed) {
        List<Integer> list = null;
        JsonElement selectedTarget = passed ? this.targetsPassed : this.targets;
        if (selectedTarget != null && !(selectedTarget instanceof JsonArray)) {
            list = GsonUtil.getDefaultGson().fromJson(selectedTarget, Targets.class).appCaptionEdits;
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
