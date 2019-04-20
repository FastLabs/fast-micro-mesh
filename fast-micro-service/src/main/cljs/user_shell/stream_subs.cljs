(ns user-shell.stream-subs
  (:require [re-frame.core :as rf]))

(defn- submit-or-cancel
  [submit-fn cancel-fn]
  (fn [ev]
    (let [key (.-key ev)
          value (.-value (.-target ev))
          action-fn (case key
                      "Enter" (fn [v] (submit-fn v) (.preventDefault ev))
                      "Escape" cancel-fn
                      identity)]
      (action-fn value))))


;; TODO: review this with on key up and on key change
(defn sub-insert
  [{:keys [on-submit on-cancel]}]
  [:div
   [:input {:type      :text
            :onKeyDown (submit-or-cancel on-submit on-cancel)}]])

(defn delete-sub-streams
  [sub]
  (rf/dispatch [:delete-subs sub]))


(defn sub-item
  [sub]
  [:li.list-group-item
   [:div.container (:name sub)]
   [:button.close {:type :button
                   :on-click #(delete-sub-streams sub)} "Remove"]])
    ;[:span {:aria-hidden true}
     ;[:i.zmdi.zmdi-close]]])



(defn sub-list []
  [:div {:style {:margin-top 10}}
   [:span "Active subscriptions"
    [:div
     (let [subs @(rf/subscribe [:all-subs])]
       [:ul.list-group
        (for [sub subs]
          ^{:key (str "item-" (:name sub))} [sub-item sub])])]]])

(defn subs-manager-view []
  [:div.card
   [:div.header
    [:h4 "Manage Subscription"]]
   [:div.body
    [sub-insert {:on-submit (fn [val] (rf/dispatch [:new-subscription val]))}]
    [sub-list]]])
