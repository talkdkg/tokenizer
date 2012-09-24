builder.router {
    /*
     * Static resources.
     */
    directory(uri: "/static", root: "module:/static/")

    /*
     * In prototype mode, first go to the pages.
     */
    mode(uri: "", passThrough: true, when: "prototype") {
        pages(root: "pages")
    }

    /*
     * Custom-implemented resources using JAX-RS.
     */
    jaxRs(uri: "", passThrough: true) {
        jaxRsResource(scanPackages: "*")
        jaxRsProvider(scanPackages: "*")
        jaxRsGroovyScripts(path: "groovy-jax-rs")
    }

    /*
     * When not in prototype, everything not matched earlier
     * is handled by the pages.
     */
    mode(uri: "", when: "all,-prototype") {
        pages(root: "pages")
    }
}