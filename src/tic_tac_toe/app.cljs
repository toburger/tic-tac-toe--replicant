(ns tic-tac-toe.app
  (:require
   [replicant.dom :as r]
   [tic-tac-toe.game-logic :as game-logic]))

(def initial-state
  {:board [[nil nil nil]
           [nil nil nil]
           [nil nil nil]]
   :current-player :x
   :winner nil})

(defn move [[x y]]
  (fn [{:keys [board current-player]}]
    (when (game-logic/can-update-cell? board x y)
      (let [new-board          (game-logic/update-board board x y current-player)
            new-winner         (game-logic/get-winner new-board)
            new-current-player (game-logic/switch-player current-player)]
        {:board          new-board
         :current-player new-current-player
         :winner         new-winner}))))

(defn restart-game []
  initial-state)

(defonce ^:private !state (atom initial-state))

(defn player-x [props]
  [:img.PlayerX
   (merge
    {:alt "PlayerX"
     :src "./img/PlayerX.svg"}
    props)])

(defn player-o [props]
  [:img.PlayerO
   (merge
    {:alt "PlayerO"
     :src "./img/PlayerO.svg"}
    props)])

(defn no-player []
  [:span.NoPlayer])

(defn dispatch-player [player]
  [:span
   (case player
     :x (player-x {:class "PlayerX--Small"})
     :o (player-o {:class "PlayerO--Small"})
     (no-player))])

(defn game-cell [x y cell]
  [:button.Cell
   {:disabled (some? cell)
    :on {:click [[:move x y]]}}
   (case cell
     :x (player-x {})
     :o (player-o {})
     (no-player))])

(defn game-board [board]
  [:div.Board
   (map-indexed
    (fn [rowidx row]
      [:div.Board__Row
       {:key rowidx}
       (map-indexed
        (fn [colidx cell]
          ^{:key (str rowidx "-" colidx)}
          (game-cell rowidx colidx cell)) row)])
    board)])

(defn game-current-player [current-player]
  [:div.CurrentPlayer
   [:span.CurrentPlayer__Text
    "Player:"
    (dispatch-player current-player)]])

(defn game-field [board current-player]
  [:div
   (game-board board)
   (game-current-player current-player)])

(def restart-image "./img/restart.png")

(defn game-over [winner]
  [:div.GameOver
   [:img.GameOver__Image
    {:on {:click [[:restart-game]]}
     :src      restart-image
     :alt      "Restart"}]
   [:p.GameOver__Text
    (case winner
      :draw "It's a draw!"
      [:span
       "Player"
       (dispatch-player winner)
       "wins!"])]])

(defn- main-view [{:keys [board current-player winner]}]
  [:div.Content
   [:div.App
    (if winner
      (game-over winner)
      (game-field board current-player))]])

(defn- render! [state]
  (r/render
   (js/document.getElementById "app")
   (main-view state)))

(defn- event-handler [_ actions]
  (doseq [action actions]
    (let [[action-name & args] action]
      (case action-name
        :move
        (swap! !state (move args))
        :restart-game
        (reset! !state (restart-game)))))
  (render! @!state))

(defn ^{:dev/after-load true :export true} start! []
  (render! @!state))

(defn ^:export init! []
  #_(inspector/inspect "App state" !state)
  (r/set-dispatch! event-handler)
  (start!))