(ns ^:figwheel-hooks user-shell.core
  (:require
    [goog.dom :as gdom]
    [event-bus.core :as ebus]
    [re-frame.core :as rf]
    [user-shell.stream-subs :as sub-man]
    [reagent.core :as reagent :refer [atom]]))


;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Application Streamer"
                          :subs {}}))

(rf/reg-event-db
  :initialise
  (fn [_ _] {:event-count {}}))

(rf/reg-event-db
  :new-event
  (fn [db [_ message]]
    (update-in db [:event-count (:sender message)] inc 1)))

(rf/reg-sub
  :event-count
  (fn [db] (:event-count db)))

(rf/reg-sub
  :all-subs
  (fn [db] (vals(:subs db))))

(rf/reg-event-db
  :new-subscription
  (fn [db [_ new-sub]]
    (update-in db [:subs] assoc new-sub {:name new-sub})))




(defn- message-handler
  [err message] (if err
                  (prn (str "received " err))
                  (let [body (.-body message)]
                       (rf/dispatch [:new-event (js->clj body :keywordize-keys true)]))))

'(ebus/register "http://localhost:8080/tick"
   (fn [eb]
     (-> eb
       (ebus/register-handler "tick-address" message-handler)
       (ebus/register-handler "tick-address-1" message-handler))))



(defn get-app-element []
  (gdom/getElement "app"))

(defn hello-world []
    [:div
     [:h4 (str "Received events: " @(rf/subscribe [:event-count]))]])

(defn subscription-view
  []
  [:section.content
   [:div.col-lg-8
    [sub-man/subs-manager-view]]])




(defn mount [el]
  (reagent/render-component
    [subscription-view] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))
;; optionally touch your app-state to force rerendering depending on
;; your application
;; (swap! app-state update-in [:__figwheel_counter] inc)
