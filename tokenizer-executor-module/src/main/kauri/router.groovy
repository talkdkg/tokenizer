builder.router {

    // access to shared templates
    directory(
            uri: "/templates", 
            root: "module:/templates"
    )

    directory(
            uri: "/static", 
            root: "module:/static/"
    )

    pages(
            uri: "",
            root: "pages"
    )


}