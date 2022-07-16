package net.labymod.intellij.singlehotswap.hotswap;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

/**
 * All available file types for single hot-swapping
 *
 * @author LabyStudio
 */
public enum FileType {
    NONE(null, null),
    JAVA(
            "net.labymod.intellij.singlehotswap.hotswap.impl.type.JavaContext",
            "com.intellij.java"
    ),
    GROOVY(
            "net.labymod.intellij.singlehotswap.hotswap.impl.type.GroovyContext",
            "org.intellij.groovy"
    ),
    KOTLIN(
            "net.labymod.intellij.singlehotswap.hotswap.impl.type.KotlinContext",
            "org.jetbrains.kotlin"
    );

    /**
     * Hotswap implementation
     */
    private Context context;

    /**
     * Creates and instance of the given implementation class for hot-swapping if the required plugin is available
     *
     * @param className        Context implementation class
     * @param requiredPluginId The required plugin for this hotswap type. This can be null to skip the requirement.
     */
    FileType(String className, String requiredPluginId) {
        if (className == null) {
            return;
        }

        try {
            // Check if plugin is required
            if (requiredPluginId != null) {

                // Find plugin by plugin id
                @Nullable IdeaPluginDescriptor plugin = PluginManager.getInstance().findEnabledPlugin(PluginId.getId(requiredPluginId));

                // Skip implementation if not plugin is not available
                if (plugin == null || !plugin.isEnabled()) {
                    return;
                }
            }

            // Load implementation
            this.context = (Context) Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the context implementation for the current type
     *
     * @return Context implementation
     */
    public Context context() {
        return this.context;
    }

    /**
     * Find context implementation using the PSI file
     *
     * @param file PSI file
     * @return Context implementation of the given PSI file type
     */
    public static Context findContext(PsiFile file) {
        if (file != null) {
            for (FileType type : values()) {
                Context instance = type.context();
                if (instance != null && instance.isPossible(file)) {
                    return instance;
                }
            }
        }

        return NONE.context();
    }
}
