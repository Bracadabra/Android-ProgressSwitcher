package ru.vang.progressswitcher.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import ru.vang.progressswitcher.ProgressSwitcher;

public class ProgressSwitcherFromContentFragment extends Fragment implements OnClickListener {

    private ProgressSwitcher mProgressSwitcher;

    private View mContentView;

    public static ProgressSwitcherFromContentFragment newInstance() {
        final ProgressSwitcherFromContentFragment fragment
                = new ProgressSwitcherFromContentFragment();

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_progress_switcher_from_content,
                container, false);
        mContentView = view.findViewById(R.id.content_view);

        view.findViewById(R.id.content).setOnClickListener(this);
        view.findViewById(R.id.progress).setOnClickListener(this);
        view.findViewById(R.id.empty).setOnClickListener(this);
        view.findViewById(R.id.error).setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProgressSwitcher = ProgressSwitcher.fromContentView(getActivity(), mContentView);
        mProgressSwitcher.setEmptyText("Empty :\\");
        mProgressSwitcher.setErrorText("Error :(");
        mProgressSwitcher.addContentView(R.layout.view_content);
    }

    @Override
    public void onClick(final View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.progress:
                mProgressSwitcher.showProgress();
                break;
            case R.id.content:
                mProgressSwitcher.showContent();
                break;
            case R.id.empty:
                mProgressSwitcher.showEmpty();
                break;
            case R.id.error:
                mProgressSwitcher.showError();
                break;
            default:
                break;
        }
    }

}
