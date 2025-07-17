(ns tic-tac-toe.app
  (:require [gadget.inspector :as inspector]
            [replicant.dom :as r]))

(defonce ^:private !state (atom 0))

(defn- main-view [state]
  [:div
   [:button {:type "text" :on {:click [[:decrement 2]]}} "--"]
   [:button {:type "text" :on {:click [[:decrement 1]]}} "-"]
   [:span (str state)]
   [:button {:type "text" :on {:click [[:increment 1]]}} "+"]
   [:button {:type "text" :on {:click [[:increment 2]]}} "++"]])

(defn- render! [state]
  (r/render
   (js/document.getElementById "app")
   (main-view state)))

(defn- event-handler [{:replicant/keys [^js js-event] :as replicant-data} actions]
  (doseq [action actions]
    (let [[action-name & args] action]
      (case action-name
        :increment (swap! !state #(+ % (first args)))
        :decrement (swap! !state #(- % (first args))))))
  (render! @!state))

(defn ^{:dev/after-load true :export true} start! []
  (render! @!state))

(defn ^:export init! []
  #_(inspector/inspect "App state" !state)
  (r/set-dispatch! event-handler)
  (start!))