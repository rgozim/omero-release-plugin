package org.ajoberstar.reckon.gradle.internal;

import org.ajoberstar.reckon.core.VcsInventory;
import org.ajoberstar.reckon.core.Version;
import org.ajoberstar.reckon.gradle.ReckonPlugin;
import org.ajoberstar.reckon.gradle.StageOptions;
import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.util.ConfigureUtil;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class DefaultStageOptions extends BaseStageOptions implements StageOptions {

    @Inject
    public DefaultStageOptions(Project project, @Nullable Map<String, ?> args) {
        super(project);

        this.stages.convention(Arrays.asList("alpha", "beta", "rc", "final"));
        this.defaultStage.convention(getDefaultStageConvention());

        if (args != null) {
            ConfigureUtil.configureByMap(args, this);
        }
    }

    @Override
    public ListProperty<String> getStages() {
        return stages;
    }

    @Override
    public Property<String> getDefaultStage() {
        return defaultStage;
    }

    @Override
    public BiFunction<VcsInventory, Version, Optional<String>> evaluateStage() {
        return (inventory, targetNormal) -> findProperty(ReckonPlugin.STAGE_PROP, defaultStage.get());
    }

    /**
     * Default to selecting the first stage alphabetically
     *
     * @return first stage alphabetically
     */
    private Provider<String> getDefaultStageConvention() {
        return this.stages.map(strings ->
                strings.stream().sorted().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No stages supplied.")));
    }

}
