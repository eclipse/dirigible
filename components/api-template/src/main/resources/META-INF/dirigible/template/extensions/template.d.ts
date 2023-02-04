declare module "@dirigible/template" {
    
    module engines {

        interface TemplateEngine {
            /**
             * Returns the result of the generation
             * @param template
             * @param parameters
             */
            generate(template, parameters):string;

            /**
             * (mustache only) Sets the expression start symbol, default is {{
             * @param sm
             */
            setSm(sm);

            /**
             * (mustache only) Sets the expression end symbol, default is }}
             * @param em
             */
            setEm(em);
        }

        /**
         * Returns the default template engine
         */
        function getDefaultEngine():TemplateEngine;

        /**
         * Returns the Mustache template engine
         */
        function getMustacheEngine(): TemplateEngine;

        /**
         * Returns the Velocity template engine
         */
        function getVelocityEngine(): TemplateEngine;

        /**
         * Returns the Javascript template engine
         */
        function getJavascriptEngine(): TemplateEngine;

        /**
         * Returns the result of the generation
         * @param template
         * @param parameters
         */
        function generate(template, parameters):string;

        /**
         * Returns the result of the generation
         * @param location
         * @param parameters
         */
        function generateFromFile(location, parameters):string;
    }
    
}
