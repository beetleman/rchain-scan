(ns rchain-scan.core
  (:require [baking-soda.core :as b]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [rchain-scan.subscriptions.core]
            [rchain-scan.controller.core]
            [rchain-scan.ajax :as ajax]
            [rchain-scan.routing :as routing]
            [rchain-scan.sse :as sse]
            [rchain-scan.view.uptime :as uptime]
            [rchain-scan.view.blocks :as blocks])
  (:import goog.History))

; the navbar components are implemented via baking-soda [1]
; library that provides a ClojureScript interface for Reactstrap [2]
; Bootstrap 4 components.
; [1] https://github.com/gadfly361/baking-soda
; [2] http://reactstrap.github.io/

(defn nav-link [title page]
  [b/NavItem
   [b/NavLink
    {:href   (kf/path-for [page])
     :active (= page @(rf/subscribe [:nav/page]))}
    title]])

(defn navbar []
  (r/with-let [expanded? (r/atom true)]
    [b/Navbar {:dark true
               :color "dark"
               :class-name "navbar-dark bg-primary"
               :expand "md"}
     [b/NavbarBrand {:href "/"} "rchain-scan"]
     [uptime/badge]
     [b/NavbarToggler {:on-click #(swap! expanded? not)}]
     [b/Collapse {:is-open @expanded? :navbar true}
      [b/Nav {:class-name "mr-auto" :navbar true}
       [nav-link "Home" :home]
       [nav-link "Blocks" :blocks]
       [nav-link "About" :about]]]]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(rf/reg-event-fx
  ::load-about-page
  (constantly nil))

(kf/reg-controller
  ::about-controller
  {:params (constantly true)
   :start  [::load-about-page]})

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(defn home-page []
  [:div.container
   [:div.row>div.col-sm-12
    [:h2.alert.alert-info "Tip: try pressing CTRL+H to open re-frame tracing menu"]]
   (when-let [docs @(rf/subscribe [:docs])]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])

(kf/reg-chain
  ::load-home-page
  (fn [_ _]
    {:http {:method :get
            :url    "/docs"}})
  (fn [{:keys [db]} [_ docs]]
    {:db (assoc db :docs docs)}))


(kf/reg-controller
  ::home-controller
  {:params (constantly true)
   :start  [::load-home-page]})

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home home-page
    :about about-page
    :blocks blocks/page
    nil [:div ""]]])

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'root-component] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (kf/start! {:debug?         false ; set to `true` if you want have a lot of logs,
                                        ; use re-frame-10x insted
              :router         (routing/->ReititRouter routing/router)
              :chain-links    [ajax/ajax-chain sse/sse-chain]
              :initial-db     {}
              :root-component [root-component]})
  (routing/hook-browser-navigation!))
