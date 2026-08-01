// Plugins are declared only in the modules that apply them (:core, :app):
// a plugin on the root classpath makes versioned requests in subprojects
// unresolvable, and keeping Android plugins out of the root lets
// :core-only builds (e.g. sandboxes without access to Google's Maven)
// run with --configure-on-demand and no Android toolchain at all.

group = "com.mnatorres.shortgoals"
version = "0.1.0"
