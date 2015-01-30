{:uberjar {:env {:settings "settings.edn"}}
 :clojure [:default {:env {:settings "settings/clojure.edn"}}]
 :javascript [:default {:env {:settings "settings/javascript.edn"}}]
 :test {:env {:settings "settings/clojure.edn"}}}