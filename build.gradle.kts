// Android plugins are declared only in :app so that :core-only builds
// (e.g. sandboxes without access to Google's Maven) never resolve them;
// combine with --configure-on-demand to skip :app's configuration.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

group = "com.mnatorres.shortgoals"
version = "0.1.0"
