(ns gdrive-auditor.core
  (:use [clojure.pprint])
  (:use [clojure.walk])
  (:require [clojure.data.json :as json])
  (:require [google-apps-clj.google-drive :as gdrive])
  (:require [google-apps-clj.credentials :as gauth])
  (:require [clojure.term.colors :refer :all])
  (:require [clojure.edn :as edn])
  (:gen-class))

(def me ((edn/read-string (slurp "config/defaults.edn")) :exclude-user))
(def creds-file-location "config/google-creds.edn")

(defn read-directory
  [creds path]
  (map
   #(hash-map 
     :id (% :id)
     :title (% :title)
     :shared (% :shared)
     :mime-type (% :mime-type))
   (gdrive/list-files! creds path)))

(defn get-collaborators
  [creds file]
  (if (file :shared)
    (remove #(= % me)
            (map #(% :email-address)
                 (gdrive/get-permissions! creds (file :id))))))

(defn is-directory?
  [file]
  (= (file :mime-type) "application/vnd.google-apps.folder"))

(defn print-file
  [file collaborators depth]
  (let [lb (if (is-directory? file) "[ " "")
        rb (if (is-directory? file) " ]" "")]
    (println
     (apply str (repeat depth "\t"))
     (if (file :shared)
       (format "%s%s%s %s"
               (yellow (on-grey (bold lb)))
               (yellow (on-grey (bold (file :title))))
               (yellow (on-grey (bold rb)))
               (yellow (pr-str collaborators)))
       (format "%s%s%s"
               lb
               (file :title)
               rb)))))

(defn walk-tree
  ([creds path]
   (walk-tree creds path 0))
  ([creds path depth]
   (let [files (read-directory creds path)]
     (doall (for [file files]
              (let [collaborators (get-collaborators creds file)]
                (if (is-directory? file)
                  (do
                    (print-file file collaborators depth)
                    (walk-tree creds (file :id) (+ depth 1)))
                  (print-file file collaborators depth))))))))

(defn persist-auth-map
  [creds auth-map]
  (let [new-creds (conj creds [:auth-map auth-map])]
    (spit creds-file-location (pr-str new-creds))))

(defn login
  []
  (let [creds (edn/read-string (slurp creds-file-location))
        response (google-apps-clj.credentials/get-auth-map creds ["https://www.googleapis.com/auth/drive"])
        auth-map (keywordize-keys
                 (into (sorted-map)
                       (map (fn [[key val]] [(clojure.string/replace key #"_" "-") val])
                            (json/read-str (.toString response)))))]
       (do (persist-auth-map creds auth-map)
           (println (format "Authentication token written to ./%s" creds-file-location))
           (println "Login successful!"))))

(defn tree
  [directory-id]
  (let [creds (edn/read-string (slurp creds-file-location))]
    (do
      (walk-tree creds directory-id)
      "")))

(defn -main [& args]
   (case (first args)
    "login" (login)
    "tree" (tree (nth args 1))))
