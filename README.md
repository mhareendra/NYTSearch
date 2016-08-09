# NYTSearch

This is an Android demo application that lets users perform filter-enabled search operations for New York Times articles. Individual articles can also be viewed in an embedded web browser. 
Time spent: 20 hours spent in total

## Completed user stories:

Required:

 * [x] User can enter a search query that will display a grid of news articles using the thumbnail and headline from the New York Times Search API. (3 points)
 * [X] User can click on "filter" icon which allows selection of advanced search options to filter articles. (3 points)
 * [X] An example of a query with filters (begin_date, sort, and news_desk) applied can be found here. Full details of the API can be found on this article search README.
 * [X] User can configure advanced search filters such as: (points included above)
 * [X] Begin Date (using a date picker)
 * [X] Sort order (oldest or newest) using a spinner dropdown
 * [X] News desk values (Arts, Fashion & Style, Sports) using checkboxes
 * [X] Subsequent searches will have any filters applied to the search results. (1 point)
 * [X] User can tap on any article in results to view the contents in an embedded browser. (2 points)
 * [X] User can scroll down "infinitely" to continue loading more news articles. The maximum number of articles is limited by the API search. (1 point)

Optional:

 * [X] Robust error handling, check if internet is available, handle error cases, network failures. (1 point)
 * [X] Use the ActionBar SearchView or custom layout as the query box instead of an EditText. (1 point)
 * [X] User can share a link to their friends or email it to themselves. (1 point)
 * [X] Replace Filter Settings Activity with a lightweight modal overlay. (2 points)
 * [X] Improve the user interface and experiment with image assets and/or styling and coloring (1 to 3 points depending on the difficulty of UI improvements)

 * [X] Stretch: Use the RecyclerView with the StaggeredGridLayoutManager to display improve the grid of image results (see Picasso guide too). (2 points)
 * [X] Stretch: For different news articles that only have text or have text with thumbnails, use Heterogenous Layouts with RecyclerView. (2 points)
 * [X] Stretch: Apply the popular ButterKnife annotation library to reduce view boilerplate. (1 point)
 * [X] Stretch: Use Parcelable instead of Serializable using the popular Parceler library. (1 point)
 * [X] Stretch: Replace Picasso with Glide for more efficient image rendering. (1 point)
 * [X] Stretch: Switch to using retrolambda expressions to cleanup event handling blocks. (1 point)
 * [X] Stretch: Leverage the popular GSON library to streamline the parsing of JSON data. (1 point)
 * [X] Stretch: Consume the New York Times API using the popular Retrofit networking library instead of Android Async HTTP. (3 points)

## Notes:

Added animation effects (combination of SlideIn, Alpha In and ScaleIn) to the recycler view.

Added Zoom-in effect on clicking an item in the search results and Zoom-out effect when exiting the webview activity.

Added Swipe-to-refresh on search activity

Added Progress bar indicator that is active when request is in progress

Added Card View to improve the appearance of items in the staggered Grid View

Added a few additional filter options (news desk values: Cars, education, books)

Implemented support for rounded image corners using Glide but ended up not using it because of Card View UI

Attempted Parceler library but could not get it to work on user defined classes and hence ended up not using it.


## Walkthrough of all user stories:

https://vimeo.com/177018509


## License

    Copyright [2016] [Hareendra Manuru]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
