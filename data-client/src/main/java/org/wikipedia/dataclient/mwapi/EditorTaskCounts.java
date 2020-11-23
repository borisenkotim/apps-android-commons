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
    public Map<String, Integer> getDescriptionEditsPerLanguage() {
        Map<String, Integer> editsPerLanguage = null;
        if (counts != null && !(counts instanceof JsonArray)) {
            editsPerLanguage = GsonUtil.getDefaultGson().fromJson(counts, Counts.class).appDescriptionEdits;
        }
        return editsPerLanguage == null ? Collections.emptyMap() : editsPerLanguage;
    }

    @NonNull
    public List<Integer> getDescriptionEditTargetsPassed() {
        List<Integer> passedList = null;
        if (targetsPassed != null && !(targetsPassed instanceof JsonArray)) {
            passedList = GsonUtil.getDefaultGson().fromJson(targetsPassed, Targets.class).appDescriptionEdits;
        }
        return passedList == null ? Collections.emptyList() : passedList;
    }

    public int getDescriptionEditTargetsPassedCount() {
        List<Integer> targetList = getDescriptionEditTargets();
        List<Integer> passedList = getDescriptionEditTargetsPassed();
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
    public List<Integer> getDescriptionEditTargets() {
        List<Integer> targetList = null;
        if (targets != null && !(targets instanceof JsonArray)) {
            targetList = GsonUtil.getDefaultGson().fromJson(targets, Targets.class).appDescriptionEdits;
        }
        return targetList == null ? Collections.emptyList() : targetList;
    }

    @NonNull
    public Map<String, Integer> getCaptionEditsPerLanguage() {
        Map<String, Integer> editsPerLanguage = null;
        if (counts != null && !(counts instanceof JsonArray)) {
            editsPerLanguage = GsonUtil.getDefaultGson().fromJson(counts, Counts.class).appCaptionEdits;
        }
        return editsPerLanguage == null ? Collections.emptyMap() : editsPerLanguage;
    }

    public int getCaptionEditTargetsPassedCount() {
        List<Integer> targetList = getCaption(false);
        List<Integer> passedList = getCaption(true);
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
    public List<Integer> getCaption(Boolean passed) {
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
