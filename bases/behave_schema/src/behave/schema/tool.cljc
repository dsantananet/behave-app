(ns behave.schema.tool
  (:require [clojure.spec.alpha  :as s]
            [behave.schema.utils :refer [valid-key? zero-pos?]]))


(s/def :tool/name            string?)
(s/def :tool/order           zero-pos?)
(s/def :tool/translation-key valid-key?)
(s/def :tool/help-key        valid-key?)
(s/def :tool/subtools        set?)

(s/def :behave/module (s/keys :req [:bp/uuid
                                    :tool/name
                                    :tool/order
                                    :tool/translation-key
                                    :tool/help-key]
                              :opt [:tool/subtools]))

(def schema
  [{:db/ident       :tool/name
    :db/doc         "Tool's name."
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :tool/order
    :db/doc         "Tool's order."
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident       :tool/subtools
    :db/doc         "Tool's subtools."
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many}

   {:db/ident       :tool/lib-ns
    :db/doc         "Tool's cljs namespace where functions are defined."
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :tool/translation-key
    :db/doc         "Tool's translation key."
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident       :tool/help-key
    :db/doc         "Tool's help key."
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}])

(comment
  (s/valid? :behave/module {:bp/uuid              (str (random-uuid))
                            :tool/name            "Relative Humidity"
                            :tool/order           1
                            :tool/translation-key "behaveplus:relative-humidity"
                            :tool/help-key        "behaveplus:relative-humidity:help"}))
