(ns behave.settings.subs
  (:require [re-frame.core    :as rf]
            [datascript.core  :as d]
            [behave.vms.store :refer [vms-conn]]))

(rf/reg-sub
 :settings/show-settings-selector?
 (fn [db _]
   (let [state (get-in db [:state :sidebar :*tools-or-settings])]
     (= state :settings))))

(rf/reg-sub
 :settings/local-storage-units
 (fn [_]
   (rf/subscribe [:local-storage/get]))

 (fn [local-storage]
   (:units local-storage)))

(rf/reg-sub
 :settings/cached-unit
 (fn []
   (rf/subscribe [:settings/local-storage-units]))

 (fn [local-storage [_ domain-uuid]]
   (:unit-uuid (get local-storage domain-uuid))))

(rf/reg-sub
 :settings/cached-decimal
 (fn []
   (rf/subscribe [:settings/local-storage-units]))

 (fn [local-storage [_ v-uuid]]
   (:decimals (get local-storage v-uuid))))

(rf/reg-sub
 :settings/all-units+decimals
 (fn []
   (rf/subscribe [:settings/local-storage-units]))

 (fn [cached-units _]
   (let [domain-units (->> (d/q '[:find ?domain-set-name ?domain-name ?domain-uuid ?dimension-uuid ?unit-uuid ?decimals
                                  :where
                                  [?ds :domain-set/name ?domain-set-name]
                                  [?ds :domain-set/domains ?d]
                                  [?d :domain/name ?domain-name]
                                  [?d :bp/uuid ?domain-uuid]
                                  [(get-else $ ?d :domain/dimension-uuid "N/A") ?dimension-uuid]
                                  [?d :domain/native-unit-uuid ?unit-uuid]
                                  [(get-else $ ?d :domain/decimals "N/A") ?decimals]]
                                @@vms-conn)
                           (sort-by (juxt first second)))]
     (->> (map (fn [[v-domain v-name v-uuid v-dimension-uuid default-unit-uuid default-decimals]]
                 (let [{:keys [unit-uuid decimals]} (get cached-units v-uuid)]
                   (-> [v-domain v-name v-uuid v-dimension-uuid]
                       (conj (or unit-uuid default-unit-uuid))
                       (conj (or decimals default-decimals)))))
               domain-units)
          (group-by first)))))

(rf/reg-sub
 :settings/show-disclaimer?
 (fn [_]
   (rf/subscribe [:local-storage/get]))

 (fn [local-storage]
   (if (contains? local-storage :show-disclaimer?)
     (:show-disclaimer? local-storage)
     true)))
