# Assignment: Week 2 - *News Search article app*

**News Search article app** New York Times News Search article app which allows a user to find old articles.

Submitted by: **Praveen Shangunathan**

Time spent: **20** hours spent in total

## User Stories

The following **required** functionality is completed:

* [X] User can enter a search query that will display a grid of news articles using the thumbnail and headline from the New York Times Search API.
    * User enters the query into a [SearchView](http://guides.codepath.com/android/Extended-ActionBar-Guide#adding-searchview-to-actionbar) within the app bar.
* [X] User can click on "settings" which allows selection of advanced search options to filter results.
    * Used Parcelable instead of Serializable for model objects like Article.
* [X] User can configure advanced search filters.
    * Begin Date (using a [date picker](http://guides.codepath.com/android/Using-DialogFragment#displaying-date-or-time-picker-dialogs))
    * News desk values (Arts, Fashion & Style, Sports)
    * Sort order (oldest or newest)
* [X] Subsequent searches will have any filters applied to the search results.
* [X] User can tap on any article in results to view the contents in an embedded browser.
* [ ] User can scroll down "infinitely" to continue loading more news articles. The maximum number of articles is limited by the API search.
* [ ] User can share a link to their friends or email it to themselves.

The following **optional** advanced user stories are implemented:


The following **additional** features are implemented:

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Starting the emulator successfully all the time was an issue. Once I had to reboot my machine to get this started.

## License

    Copyright [2017] [Praveen Shangunathan]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
