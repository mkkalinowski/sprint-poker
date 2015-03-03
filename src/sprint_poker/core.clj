(ns sprint-poker.core
  (:require [clojure.data.json :as json]
            [clojure.algo.generic.functor :refer [fmap]]
            [compojure.core :refer :all]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.server.standalone :refer [serve]]
            [ring.util.response :refer :all])
  (:gen-class :main true))

(def data (atom {}))

(defn new-id
  "Create a new randomly generated ID."
  []
  (str (java.util.UUID/randomUUID)))

(defn create-new-room
  "Create a new room and redirect to it."
  [req]
  (let [room-id (new-id)]
    (println "create-new-room:" room-id)
    (swap! data #(assoc % room-id {:revealed false :votes {}}))
    (response "")
    (redirect (str "/" room-id))))

(defn get-votes
  "Return a map of all votes in the room."
  [{{room-id :room-id} :route-params {user-id :id} :session}]
  {:pre [(string? room-id) (string? user-id)]}
  (let [room-data (get @data room-id)]
    (println "get-votes" room-id user-id room-data)
    (if (:revealed room-data)
      (-> (response (json/write-str (:votes room-data)))
          (content-type "application/json"))
      ; Show who has voted, but obscure the points themselves.
      (-> (fmap #(assoc % :points "-") (:votes room-data))
          (or {})
          (json/write-str)
          (response)
          (content-type "application/json")))))

(defn put-vote
  [{{room-id :room-id name "name" points "points"} :params {user-id :id} :session :as req}]
  {:pre [(string? room-id) (string? user-id) (string? points)]}
  (println "put-vote" room-id user-id points)
  (swap! data #(assoc-in % [room-id :votes user-id] {:name name :points points}))
  (get-votes req))

(defn reveal-votes
  [{{room-id :room-id} :route-params {user-id :id} :session :as req}]
  {:pre [(string? room-id) (string? user-id)]}
  (println "reveal-votes" room-id user-id)
  (swap! data #(assoc-in % [room-id :revealed] true))
  (get-votes req))

(defn reset-votes
  [{{room-id :room-id} :route-params {user-id :id} :session :as req}]
  {:pre [(string? room-id) (string? user-id)]}
  (println "reset-votes" room-id user-id)
  (swap! data #(assoc % room-id {:revealed false :votes {}}))
  (get-votes req))

(defroutes app
  ; GET / => redirect to /{new room id}
  (GET "/" [] create-new-room)
  (context "/:room-id" [room-id]
    ; GET /{room-id} => render index.html
    (GET "/" [] (file-response "resources/index.html"))
    ; GET /{room-id}/votes => get-votes
    (GET "/votes" [] get-votes)
    ; PUT /{room-id}/vote => put-vote
    (PUT "/vote" [] put-vote)
    ; POST /{room-id}/reveal => reveal-votes
    (POST "/reveal" [] reveal-votes)
    ; POST /{room-id}/reset => reset-votes
    (POST "/reset" [] reset-votes)))

(defn wrap-user-id
  "Assigns the user a randomly generated ID."
  [handler]
  (fn [req]
    (let [id (or (get-in req [:session :id])
                 (new-id))
          req' (assoc-in req [:session :id] id)
          res (handler req')]
      (assoc-in res [:session :id] id))))

(def handler
  (-> app
      (wrap-params)
      (wrap-resource "/")
      (wrap-file-info)
      (wrap-user-id)
      (wrap-session)))

(defn -main [] (serve handler))
