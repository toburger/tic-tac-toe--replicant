(ns tic-tac-toe.app
  (:require [gadget.inspector :as inspector]
            [replicant.dom :as r]))

(def initial-state
  {:count 0
   :board [[nil nil nil]
           [nil nil nil]
           [nil nil nil]]
   :current-player :x
   :winner nil})

(defonce ^:private !state (atom initial-state))

(defn counter [count]
  [:div
   [:button {:type "text" :on {:click [[:decrement 2]]}} "--"]
   [:button {:type "text" :on {:click [[:decrement 1]]}} "-"]
   [:span (str count)]
   [:button {:type "text" :on {:click [[:increment 1]]}} "+"]
   [:button {:type "text" :on {:click [[:increment 2]]}} "++"]])

(defn- main-view [{:keys [count]}]
  (counter count))

(defn- render! [state]
  (r/render
   (js/document.getElementById "app")
   (main-view state)))

(defn- event-handler [{:replicant/keys [^js js-event] :as replicant-data} actions]
  (doseq [action actions]
    (let [[action-name & args] action]
      (case action-name
        :increment (swap! !state update :count #(+ % (first args)))
        :decrement (swap! !state update :count #(- % (first args))))))
  (render! @!state))

(defn ^{:dev/after-load true :export true} start! []
  (render! @!state))

(defn ^:export init! []
  #_(inspector/inspect "App state" !state)
  (r/set-dispatch! event-handler)
  (start!))