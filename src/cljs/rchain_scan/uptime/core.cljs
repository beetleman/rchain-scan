(ns rchain-scan.uptime.core
  (:require [goog.object :as obj]
            [re-frame.core :as rf]))


(defn event-source->cljs [e]
  {:data          (obj/get e "data")
   :type          (obj/get e "type")
   :message-event e})


(defn event-source
  ([url]
   (event-source url {}))
  ([url {:keys [on-message on-error] :or {on-message js/console.log
                                          on-error   js/console.error}}]
   (let [e (js/EventSource. url)]
     (obj/set e "onmessage" (comp on-message event-source->cljs))
     (obj/set e "onerror" on-error)
     e)))


(rf/reg-fx
  :event-source
  (fn [{:keys [url message-event error-event]
        :or   {error-event [:common/set-error]}}]
    (event-source url
                  {:on-message (fn [m]
                                 (rf/dispatch (conj message-event m)))
                   :on-error   (fn [error]
                                 (rf/dispatch (conj error-event error)))})))
