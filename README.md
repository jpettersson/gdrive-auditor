# gdrive-auditor

A tool that gives you a birds-eye view over sharing settings in Google Drive folders. This project is not feature complete in any way, so consider it a source of inspiration rather than a polished tool. Feel free to contribute features & ideas. PRs are welcome!

## Example usage

![Alt text](public/gdrive-auditor.png?raw=true "Example usage")

In this example, the yellow files and folders are shared with person.a, person.b and person.c. Folder1 and Folder2 are printed in white and without annotated email addresses, meaning they are not shared with anyone.

## Setup

### Prerequisites

* You need to have Clojure & [Leiningen](https://leiningen.org/) installed

### Authentication

To give the tool access to your Drive files you need to create an OAuth client ID in Google Cloud and authorize using your user account.

1. Using the Google [Developer's Console](https://console.developers.google.com/)
2. Select an existing project, or create a new one if needed
3. Enable the Google Drive API in the [API Library](https://console.developers.google.com/apis/library)
4. Create a new credential of type "OAth client ID" on the [Credentials page](https://console.developers.google.com/apis/credentials)
5. Using information from the credential, create the file `config/google-creds.edn` using the following template:

```edn
{:client-id "MY_CLIENT_ID"
 :client-secret "MY_CLIENT_SECRET"
 :redirect-uris ["https://localhost"]
 :auth-map {}}
```

6. Run the login command
`lein run login`

7. Open the printed URL in a browser and go through the authorization flow
8. Copy the resulting code and paste it into the terminal, followed by enter. The auth tokens will be automatically written to the `:auth-map` key in the `config/google-creds.edn` file

9. Rename the file `config/example-defaults.edn` to `config/defaults.edn` and add your email address, so that it can be filtered out when visualizing Drive collaborators.

## Usage

Run the tree command with a Google Drive folder ID as an argument

`lein run tree MY_FOLDER_ID`

The folder ID is the last segment of a Google Drive URL. 
Example `https://drive.google.com/drive/u/0/folders/MY_FOLDER_ID`

## TODO & Ideas (PRs welcome!)

* Add support for JSON output

## License

Copyright © 2018 Jonathan Pettersson

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
 
