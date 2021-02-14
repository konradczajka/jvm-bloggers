package com.jvm_bloggers.frontend.public_area.search_posts;

import static com.jvm_bloggers.utils.DateTimeUtilities.DATE_TIME_FORMATTER;

import com.jvm_bloggers.domain.query.searched_blog_post_for_listing.SearchedBlogPostForListing;
import com.jvm_bloggers.domain.query.searched_blog_post_for_listing.SearchedBlogPostForListingQuery;
import com.jvm_bloggers.frontend.public_area.AbstractFrontendPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("wyszukaj")
@Slf4j
public class SearchPostsPage extends AbstractFrontendPage {

  private static final String TWITTER_HOME_URL = "https://twitter.com/";
  private final String SEARCH_FORM = "searchForm";
  private final String SEARCH_PHRASE = "searchPhrase";
  private final String DATA_VIEW = "postsListView";
  private final String WEB_MARKUP_CONTAINER = "wrapper";
  private final String NAVIGATOR = "navigator";
  private final String SUBMIT_ID = "submit";

  private final Form<SearchPostsModel> searchPostsModelForm;

  @SpringBean
  private SearchedBlogPostForListingQuery query;

  public SearchPostsPage() {
    searchPostsModelForm = new Form<>(SEARCH_FORM, new CompoundPropertyModel<>(new SearchPostsModel()));
    add(searchPostsModelForm);
    initSearchPhraseInput();
    initSubmitButton();
    initDataView();
  }

  private void initSearchPhraseInput() {
    var searchInput = new TextField<String>(SEARCH_PHRASE);
    searchPostsModelForm.add(searchInput);
  }

  private void initSubmitButton() {
    var searchSubmitButton = new AjaxButton(SUBMIT_ID, searchPostsModelForm) {
      @Override
      protected void onSubmit(AjaxRequestTarget target) {
        log.info("submit an ajax request {} ", searchPostsModelForm.getModelObject());
        searchPostsModelForm.setOutputMarkupId(true);
        target.add(searchPostsModelForm);
      }

      @Override
      protected void onError(AjaxRequestTarget target) {
        log.error("an ajax request error");
        super.onError(target);
      }
    };
    searchPostsModelForm.add(searchSubmitButton);
  }

  private void initDataView() {
    WebMarkupContainer container = new WebMarkupContainer(WEB_MARKUP_CONTAINER);
    var requestHandler = new SearchBlogPostsRequestHandler(searchPostsModelForm, query);
    searchPostsModelForm.add(container);
    var dataView = dataView(requestHandler);
    container.add(dataView);
    dataView.setItemsPerPage(SearchBlogPostsRequestHandler.PAGE_SIZE);
    var pagingNavigator = new AjaxPagingNavigator(NAVIGATOR, dataView);
    searchPostsModelForm.add(pagingNavigator);
  }


  private DataView<SearchedBlogPostForListing> dataView(IDataProvider<SearchedBlogPostForListing> requestHandler) {
    return new DataView<>(DATA_VIEW, requestHandler) {
      @Override
      protected void populateItem(Item<SearchedBlogPostForListing> item) {
        var post = item.getModelObject();
        item.add(new Label("author", post.getAuthor()));
        item.add(new ExternalLink("link", post.getUrl(), post.getTitle()));
        item.add(new Label("publishedDate", post.getPublishedTime().format(DATE_TIME_FORMATTER)));
        item.add(new ExternalLink("twitter",
              TWITTER_HOME_URL + post.getAuthorTwitterHandle(),
              post.getAuthorTwitterHandle()
            ).setVisible(StringUtils.isNotEmpty(post.getAuthorTwitterHandle()))
        );
      }
    };
  }

  @Override
  protected String getPageTitle() {
    return "Wyszukaj posty";
  }
}
